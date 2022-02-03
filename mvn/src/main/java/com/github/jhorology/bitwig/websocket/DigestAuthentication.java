/*
 * The MIT License
 *
 * Copyright 2020 masafumi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.jhorology.bitwig.websocket;

import com.github.jhorology.bitwig.logging.LoggerFactory;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.slf4j.Logger;

/**
 * A class for digest challenge and response authentication.
 */
public class DigestAuthentication {

  private static final int CHALLENGE_CLOSE_CODE = 4401;
  private static final Logger LOG = LoggerFactory.getLogger(
    DigestAuthentication.class
  );
  private static final int MAX_STORE_SIZE = 200;
  private static final long CHALLENGE_VALID_PERIOD = 30000L;
  private static final String USER_NAME = "bitwig";

  private static final class Challenge {

    private final InetAddress remoteAddress;
    private final String uri;

    @Expose
    private final String realm = "biwig-websocket-rpc"; // for future changes

    @Expose
    private final String nonce;

    @Expose
    private final String algorithm = "md5"; // for future changes

    @Expose
    private final String qop = "auth"; // for future changes

    private final long timestamp;
    private final AtomicInteger nc;
    private String json = null;

    private Challenge(InetAddress remoteAddress, String uri) {
      this.remoteAddress = remoteAddress;
      this.uri = uri;
      this.nonce = RandomStringUtils.randomAlphanumeric(21);
      this.timestamp = System.currentTimeMillis();
      this.nc = new AtomicInteger(0);
    }

    public InetAddress getRemoteAddress() {
      return remoteAddress;
    }

    public String getUri() {
      return uri;
    }

    public String getRealm() {
      return realm;
    }

    public String getNonce() {
      return nonce;
    }

    public String getAlgorithm() {
      return algorithm;
    }

    public String getQop() {
      return qop;
    }

    public Long getTimestamp() {
      return timestamp;
    }

    public String incrementAndGetNc() {
      int num = nc.incrementAndGet();
      String strNum = "00000000" + Integer.toHexString(num).toLowerCase();
      return strNum.substring(strNum.length() - 8);
    }

    public boolean isValid() {
      long now = System.currentTimeMillis();
      return (timestamp + CHALLENGE_VALID_PERIOD) <= now;
    }

    public String toJson() {
      if (json == null) {
        Gson gson = new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .create();
        json = gson.toJson(this);
      }
      return json;
    }
  }

  private final Map<String, Challenge> store;
  private final Predicate<String> authRequired;
  private final Supplier<String> password;

  /**
   * Constructor.
   * @param config
   */
  DigestAuthentication(
    Predicate<String> authRequired,
    Supplier<String> password
  ) {
    store = new ConcurrentHashMap<>();
    this.authRequired = authRequired;
    this.password = password;
  }

  /**
   * if needed, generate challenge and return it as reason for close.
   * @param conn
   * @param request
   * @return
   */
  public boolean challenge(WebSocket conn, ClientHandshake request) {
    String resourceDescriptor = request.getResourceDescriptor();
    if (!authRequired.test(resourceDescriptor)) {
      return false;
    }
    if ("/auth".equals(resourceDescriptor)) {
      // first challenge
      String host = request.getFieldValue("Host");
      String uri = String.format("ws://%s%s", host, resourceDescriptor);

      Challenge challenge = createAndStoreChallenge(
        conn.getRemoteSocketAddress().getAddress(),
        uri
      );
      conn.close(CHALLENGE_CLOSE_CODE, challenge.toJson());
      return true;
    }
    return false;
  }

  /**
   * authenticate WebSocket connection.
   * @param conn
   * @param request
   * @return
   */
  public boolean authenticate(WebSocket conn, ClientHandshake request) {
    String resourceDescriptor = request.getResourceDescriptor();
    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "Authentication is {}.",
        authRequired.test(resourceDescriptor) ? "on" : "off"
      );
    }
    if (!authRequired.test(resourceDescriptor)) {
      return true;
    }
    String host = request.getFieldValue("Host");
    if (host == null || host.length() == 0) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("connection[{}] was rejected. Host field is missing.", conn);
      }
      return false;
    }
    String uri = String.format("ws://%s%s", host, resourceDescriptor);
    if ("/auth".equals(resourceDescriptor)) {
      // challenge
      // there is no way to send challenge in handshake process.
      if (LOG.isTraceEnabled()) {
        LOG.trace("connection[{}] was temporally accepted to challenge.", conn);
      }
      return true;
    } else if (
      resourceDescriptor != null && resourceDescriptor.startsWith("/auth?")
    ) {
      // has response query parameters
      return validateResponse(conn, uri.split("\\?")[1]);
    }
    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "connection[{}] was rejected. uri [{}] is not accepted.",
        conn,
        uri
      );
    }
    return false;
  }

  private Challenge createAndStoreChallenge(
    InetAddress remoteAddress,
    String uri
  ) {
    Challenge challenge = new Challenge(remoteAddress, uri);
    removeExpiredChallenge();
    store.put(challenge.getNonce(), challenge);
    return challenge;
  }

  private void removeExpiredChallenge() {
    store
      .values()
      .stream()
      .filter(c -> !c.isValid())
      .map(c -> c.getNonce())
      .collect(Collectors.toList())
      .forEach(n -> {
        store.remove(n);
      });
    if (LOG.isWarnEnabled() && store.size() >= MAX_STORE_SIZE) {
      LOG.warn(
        "Challenge store reached full capacity, maybe malicious attack!!!"
      );
    }
    while (store.size() >= MAX_STORE_SIZE) {
      Challenge mostOld = Collections.min(
        store.values(),
        (Challenge o1, Challenge o2) ->
          o1.getTimestamp().compareTo(o2.getTimestamp())
      );
      store.remove(mostOld.getNonce());
    }
  }

  private boolean validateResponse(WebSocket conn, String query) {
    Map<String, String> params;
    try {
      params =
        Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query);
    } catch (Exception ex) {
      if (LOG.isTraceEnabled()) {
        LOG.trace(
          "connection[{}] was rejected. format of query params is wrong.",
          conn
        );
      }
      return false;
    }
    String username = params.get("username");
    String realm = params.get("realm");
    String nonce = params.get("nonce");
    String uri = params.get("uri");
    String algorithm = params.get("algorithm");
    String response = params.get("response");
    String qop = params.get("qop");
    String nc = params.get("nc");
    String cnonce = params.get("cnonce");
    if (
      !USER_NAME.equals(params.get("username")) ||
      StringUtils.isEmpty(realm) ||
      StringUtils.isEmpty(nonce) ||
      StringUtils.isEmpty(uri) ||
      StringUtils.isEmpty(algorithm) ||
      StringUtils.isEmpty(response) ||
      StringUtils.isEmpty(qop) ||
      StringUtils.isEmpty(nc) ||
      StringUtils.isEmpty(realm) ||
      StringUtils.isEmpty(cnonce)
    ) {
      if (LOG.isTraceEnabled()) {
        LOG.trace(
          "connection[{}] was rejected. required params are missing.",
          conn
        );
      }
      return false;
    }
    Challenge challenge = store.get(nonce);
    if (challenge == null) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("connection[{}] was rejected. nonce param is missing.", conn);
      }
      return false;
    }
    if (
      !challenge
        .getRemoteAddress()
        .equals(conn.getRemoteSocketAddress().getAddress())
    ) {
      if (LOG.isTraceEnabled()) {
        LOG.trace(
          "connection[{}] was rejected. recieved response from unknown host.",
          conn
        );
      }
      return false;
    }
    if (
      !realm.equals(challenge.getRealm()) ||
      !uri.equals(challenge.getUri()) ||
      !algorithm.equals(challenge.getAlgorithm()) ||
      !qop.equals(challenge.getQop())
    ) {
      store.remove(nonce);
      if (LOG.isTraceEnabled()) {
        LOG.trace(
          "connection[{}] was rejected. params are unmatched challenge.",
          conn
        );
      }
      return false;
    }
    if (!nc.equals(challenge.incrementAndGetNc())) {
      store.remove(nonce);
      if (LOG.isTraceEnabled()) {
        LOG.trace("connection[{}] was rejected. nc param is wrong.", conn);
      }
      return false;
    }
    String a1 = DigestUtils.md5Hex(
      USER_NAME + ":" + realm + ":" + password.get()
    );
    String a2 = DigestUtils.md5Hex(":" + uri);
    String caluculatedResponse = DigestUtils.md5Hex(
      a1 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + a2
    );
    store.remove(nonce);
    if (response.equals(caluculatedResponse)) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("connection[{}] was accepted to connect.", conn);
      }
      return true;
    }
    // TODO should retry challenge?
    if (LOG.isTraceEnabled()) {
      LOG.trace("connection[{}] was rejected. failed authentication.", conn);
    }
    return false;
  }
}
