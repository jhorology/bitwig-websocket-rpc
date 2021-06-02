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
 * MERCHANTABILITY>, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import java.util.List;

/**
 * A POJO class for JSON-RPC 2.0 request message.
 * @see https://www.jsonrpc.org/specification
 */
public class BatchOrSingleRequest {

  private Request request;
  private List<Request> batch;

  /**
   * Get a single 'request' object.
   * @return
   */
  public Request getRequest() {
    return request;
  }

  /**
   * Set a single 'request' object.
   * @param request
   */
  public void setRequest(Request request) {
    this.request = request;
  }

  /**
   * Get a batch of 'request' objects.
   * @return
   */
  public List<Request> getBatch() {
    return batch;
  }

  /**
   * Set a batch of 'request' objects.
   * @param batch
   */
  public void setBatch(List<Request> batch) {
    this.batch = batch;
  }

  /**
   * Return a this request is batch or not.
   * @return
   */
  public boolean isBatch() {
    return batch != null;
  }
}
