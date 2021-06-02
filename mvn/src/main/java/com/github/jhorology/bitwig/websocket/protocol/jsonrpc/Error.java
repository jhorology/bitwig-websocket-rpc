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

/**
 * A POJO class for JSON-RPC 2.0 'error' object.
 * @see https://www.jsonrpc.org/specification
 */
public class Error {

  private int code;
  private String message;
  private Object data;

  /**
   * Construct a instance with spciefied parameters.
   * @param error
   * @param data
   */
  public Error(ErrorEnum error, Object data) {
    this.code = error.getCode();
    this.message = error.getMessage();
    this.data = data;
  }

  /**
   * Construct a instance with error enum value.
   * @param error
   */
  public Error(ErrorEnum error) {
    this(error, null);
  }

  /**
   * Get a value of 'code' property.
   * @return
   */
  public int getCode() {
    return code;
  }

  /**
   * Set a value of 'code' property.
   * @param code
   */
  public void setCode(int code) {
    this.code = code;
  }

  /**
   * Get a value of 'message' property.
   * @return
   */
  public String getMessage() {
    return message;
  }

  /**
   * Set a value of 'message' property.
   * @param message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Get a value of 'data' property.
   * @return
   */
  public Object getData() {
    return data;
  }

  /**
   * Sett a value of 'data' property.
   * @param data
   */
  public void setData(Object data) {
    this.data = data;
  }
}
