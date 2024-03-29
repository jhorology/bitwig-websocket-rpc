/*
 * Copyright (c) 2018 Masafumi Fujimaru
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.jhorology.bitwig.reflect;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Value;
import com.github.jhorology.bitwig.Config;
import com.github.jhorology.bitwig.ext.api.CollectionValue;
import com.github.jhorology.bitwig.logging.LoggerFactory;
import com.github.jhorology.bitwig.rpc.RpcEvent;
import com.github.jhorology.bitwig.rpc.RpcException;
import com.github.jhorology.bitwig.websocket.protocol.Notification;
import com.github.jhorology.bitwig.websocket.protocol.PushModel;
import com.github.jhorology.bitwig.websocket.protocol.RequestContext;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;

/**
 * A RPC event holder class.<br>
 * The return value of method is guaranteed to be implemented Value interface.
 */
public class EventHolder extends MethodHolder implements RpcEvent {

  private static final Logger LOG = LoggerFactory.getLogger(EventHolder.class);

  private static final long WAIT_HOST_TRIGGER_ON_SUBSCRIBE = 150L;

  private final List<int[]> bankIndexCombinations;
  private final Collection<WebSocket> clients;
  private final PushModel pushModel;
  private final ControllerHost host;
  private final List<PrimitiveEvent> primitiveEvents;
  private final PrimitiveEvent primitiveEvent;

  /**
   *  A primitive event class.
   */
  private class PrimitiveEvent {

    private boolean hostTriggered;
    // the last reported value from host
    private Object[] lastReportedParams;
    private final Object[] bankIndexes;
    private Value<?> value;
    private boolean collectionValue;

    /**
     * Constructor.
     * @param bankIndexes
     */
    @SuppressWarnings({ "UseSpecificCatch" })
    private PrimitiveEvent(int[] bankIndexes) {
      // TODO need to support that intermediate node has arguments other than bank indexes.
      // e.g) BooleanValue applocation.getActions(id).isEnabled();
      this.bankIndexes =
        ArrayUtils.addAll(
          new Object[] {},
          (Object[]) ArrayUtils.toObject(bankIndexes)
        );
      try {
        value = (Value<?>) invoke(this.bankIndexes);
        this.collectionValue = value instanceof CollectionValue;
        BitwigCallbacks.registerObserver(value, this::onValueChanged);
        // since API 10 counter based. unsubscribe called more times than subscribe
        // value.unsubscribe();
      } catch (Exception ex) {
        setError(ex, "Failed registering observer.");
        LOG.error(event() + " event: Failed registering observer.", ex);
      }
    }

    /**
     * post event to all subscribe[rs.
     * @param params
     */
    private void onValueChanged(Object[] params) {
      hostTriggered = true;
      lastReportedParams = params;
      if (!clients.isEmpty() && pushModel != null) {
        pushModel.push(newNotification(params), clients);
      }
    }

    private void internalSubscribe(WebSocket client) {
      if (syncSubscribedState() && !collectionValue) {
        // if Subscribable#subscribed() was called, (it's meaning first client of subscribers list.)
        // host may trigger callback, but it's only for the value that has changed since last reported.
        // so need to notify current value to the client if host will not trigger callback.
        notifyCurrentValueIfHostNotTriggered(client);
      } else {
        // should send a current value to the client comming after the first one.
        notifyCurrentValue();
      }

      if (LOG.isDebugEnabled()) {
        LOG.debug(
          "[{}] event has been subscribed by {}",
          event(),
          client(client)
        );
      }
    }

    /**
     * Sync subscription states between RPC and Bitwig Studio.
     * @return host side state will change to subscribed
     */
    @SuppressWarnings({ "UseSpecificCatch" })
    private boolean syncSubscribedState() {
      try {
        if (clients.isEmpty() && value.isSubscribed()) {
          value.unsubscribe();
          return false;
        }
        if (!clients.isEmpty() && !value.isSubscribed()) {
          hostTriggered = false;
          value.subscribe();
          return true;
        }
      } catch (Exception ex) {
        setError(ex, "Fialed syncing subscribed state.");
        LOG.error(event() + " event: Fialed syncing subscribed state.", ex);
        throw new RpcException(ex);
      }
      return false;
    }

    /**
     * post current value to client.
     */
    private void notifyCurrentValueIfHostNotTriggered(WebSocket client) {
      if (pushModel != null) {
        host.scheduleTask(
          () -> {
            if (hostTriggered || lastReportedParams == null) {
              return;
            }
            post(lastReportedParams, client);
          },
          WAIT_HOST_TRIGGER_ON_SUBSCRIBE
        );
      }
    }

    /**
     * post current value to client.
     */
    private void notifyCurrentValue() {
      // do not post message directly in here.
      // 'cause message should be sent at after Request/Reponse sequence.
      if (collectionValue) {
        ((CollectionValue<?>) value).values()
          .forEach(v -> {
            RequestContext
              .getContext()
              .addNotification(newNotification(new Object[] { v }));
          });
      } else if (lastReportedParams != null) {
        RequestContext
          .getContext()
          .addNotification(newNotification(lastReportedParams));
      }
    }

    /**
     * post event to specified client.
     */
    private void post(Object[] params, WebSocket client) {
      if (clients.contains(client) && pushModel != null) {
        pushModel.push(newNotification(params), client);
      }
    }

    private void clear() {
      if (clients.size() >= 0 && value.isSubscribed()) {
        value.unsubscribe();
      }
    }

    /**
     * Create a new notification message.
     * @param params
     */
    private Notification newNotification(Object[] params) {
      if (bankIndexes.length > 0) {
        // to Object array
        params = ArrayUtils.addAll((Object[]) bankIndexes, params);
      }
      return new Notification(absoluteName, params);
    }

    private String event() {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      sb.append(absoluteName);
      if (bankIndexes.length > 0) {
        sb.append(" bank:");
        sb.append(ArrayUtils.toString(bankIndexes));
      }
      sb.append("]");
      return sb.toString();
    }
  }

  /**
   * Constructor.
   * @param config
   * @param method
   * @param nodeType
   * @param parantNode
   * @param bankItemCount
   * @param pushModel
   * @param host
   */
  EventHolder(
    Config config,
    Method method,
    Class<?> nodeType,
    RegistryNode parantNode,
    int bankItemCount,
    ControllerHost host,
    PushModel pushModel
  ) {
    super(config, method, nodeType, parantNode, bankItemCount);
    // clients = new LinkedList<>();
    this.host = host;
    this.pushModel = pushModel;
    this.clients = new ArrayList<>();
    this.bankIndexCombinations =
      bankDimension.length > 0
        ? createBankIndexCombinations(bankDimension)
        : null;
    if (bankIndexCombinations != null) {
      this.primitiveEvents =
        bankIndexCombinations
          .stream()
          .map(combination -> new PrimitiveEvent(combination))
          .collect(Collectors.toList());
      this.primitiveEvent = null;
    } else {
      this.primitiveEvents = null;
      this.primitiveEvent = new PrimitiveEvent(new int[] {});
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void subscribe(WebSocket client) {
    if (getError() != null) {
      throw new RpcException(getError());
    }
    if (!clients.contains(client)) {
      clients.add(client);
      if (primitiveEvent != null) {
        primitiveEvent.internalSubscribe(client);
      } else {
        primitiveEvents.forEach(e -> e.internalSubscribe(client));
      }
      if (LOG.isTraceEnabled()) {
        LOG.trace(
          "[{}] event has been subscribed by {}.",
          absoluteName,
          client(client)
        );
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unsubscribe(WebSocket client) {
    if (getError() != null) {
      throw new RpcException(getError());
    }
    if (clients.remove(client)) {
      syncSubscribedState();
      if (LOG.isTraceEnabled()) {
        LOG.trace(
          "[{}] event has been unsubscribed by {}.",
          absoluteName,
          client(client)
        );
      }
    }
  }

  void disconnect(WebSocket client) {
    boolean removed = clients.remove(client);
    if (removed) {
      syncSubscribedState();
      if (LOG.isTraceEnabled()) {
        LOG.trace(
          "subscriber of [{}] event has been disconnected. client:{}",
          absoluteName,
          client(client)
        );
      }
    }
  }

  /**
   * clear this instance.
   */
  @Override
  void clear() {
    if (primitiveEvent != null) {
      primitiveEvent.clear();
    } else {
      primitiveEvents.forEach(e -> e.clear());
      primitiveEvents.clear();
    }
    if (bankIndexCombinations != null) {
      bankIndexCombinations.clear();
    }
    clients.clear();
  }

  /**
   * create a report object for this class.
   * @param eh
   * @return
   */
  Object reportRpcEvent() {
    Map<String, Object> report = new LinkedHashMap<>();
    report.put("event", absoluteName);
    if (bankDimension.length > 0) {
      report.put("bankDimension", bankDimension);
    }
    if (getError() != null) {
      report.put("error", getError());
    }
    return report;
  }

  /**
   * Sync state of event subscription between RPC and Bitwig Studio.
   * @return host side state will change to subscribed
   */
  private void syncSubscribedState() {
    if (primitiveEvent != null) {
      primitiveEvent.syncSubscribedState();
    } else {
      primitiveEvents.forEach(e -> e.syncSubscribedState());
    }
  }

  private String client(WebSocket client) {
    if (client == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    sb.append(client.toString());
    if (client.getRemoteSocketAddress() != null) {
      sb.append(", ");
      sb.append(client.getRemoteSocketAddress().toString());
    }
    sb.append("]\n number of subscriber(s):");
    sb.append(clients.size());
    sb.append(']');
    return sb.toString();
  }

  private static List<int[]> createBankIndexCombinations(int[] bankDimension) {
    List<int[]> combinations = new ArrayList<>();
    for (int size : bankDimension) {
      List<int[]> newCombinations = new ArrayList<>();
      for (int element = 0; element < size; element++) {
        if (combinations.isEmpty()) {
          int[] combination = { element };
          newCombinations.add(combination);
        } else {
          for (int[] combination : combinations) {
            int[] newCombination = ArrayUtils.add(combination, element);
            newCombinations.add(newCombination);
          }
        }
      }
      combinations = newCombinations;
    }
    return combinations;
  }
}
