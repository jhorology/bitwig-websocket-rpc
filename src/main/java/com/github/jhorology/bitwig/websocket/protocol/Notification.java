package com.github.jhorology.bitwig.websocket.protocol;

/**
 *  A class to use for JSON serialization of notification message.
 *  Sending notification to client is not specied in JSON-RPC 2.0 specifiations.
 *  this aims to support 'rpc-websockets' javascript module.
 *  @see https://github.com/elpheria/rpc-websockets
 */
public class Notification {
    private String notification;
    private Object params;

    /**
     * construct a notification message.
     * @return
     */
    public Notification() {
    }

    /**
     * construct a notification message with message and parameters.
     * @param notification
     * @param params
     * @return
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
     * set a notification message.
     * @param notification
     */
    public void setNotifictaion(String notification) {
        this.notification = notification;
    }

    /**
     * get an array of parameters.
     * @return
     */
    public Object getParams() {
        return params;
    }

    /**
     * set an array of parameters.
     * @param params
     */
    public void setParams(Object params) {
        this.params = params;
    }
}
