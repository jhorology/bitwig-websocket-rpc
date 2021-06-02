package com.github.jhorology.bitwig.websocket.protocol;

/**
 *  A POJO class that represents server-sent notification message.<br>
 *  Sending notification to client is not specied in JSON-RPC 2.0 specifiations.
 *  this aims to support 'rpc-websockets' javascript module.
 *  @see https://github.com/elpheria/rpc-websockets
 */
public class Notification {

  private String notification;
  private Object[] params;

  /**
   * construct a notification message.
   * @param notification
   */
  public Notification(String notification) {
    this(notification, null);
  }

  /**
   * construct a notification message with message and parameters.
   * @param notification The notification message. Basically use this as event name.
   * @param params       The array of parameters.
   */
  public Notification(String notification, Object[] params) {
    this.notification = notification;
    this.params = params;
  }

  /**
   * get a notification message.
   * @return
   */
  public String getNotifictaion() {
    return notification;
  }

  /**
   * Set a notification message.
   * @param notification
   */
  public void setNotifictaion(String notification) {
    this.notification = notification;
  }

  /**
   * Get an array of parameters.
   * @return
   */
  public Object[] getParams() {
    return params;
  }

  /**
   * Set an array of parameters.
   * @param params
   */
  public void setParams(Object[] params) {
    this.params = params;
  }
}
