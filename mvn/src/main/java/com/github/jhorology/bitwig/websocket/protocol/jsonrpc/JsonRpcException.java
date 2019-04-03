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
 * An exception class that can be contained JSON-RPC 2.0 error object.
 */
public class JsonRpcException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private final Error error;

    /**
     * Constructs this instance with error enum.
     * @param error 
     */
    public JsonRpcException(ErrorEnum error) {
        super(error.getMessage());
        this.error = new Error(error);
    }
    
    /**
     * Constructs this instance with error enum and any option data.
     * @param error 
     * @param data 
     */
    public JsonRpcException(ErrorEnum error, Object data) {
        super(error.getMessage());
        this.error = new Error(error, data);
    }
    
    /**
     * Returns JSON-RPC 2.0 error object.
     * @return 
     */
    public Error getError() {
        return error;
    }
}
