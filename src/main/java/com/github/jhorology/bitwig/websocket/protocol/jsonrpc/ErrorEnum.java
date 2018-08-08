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
 * Defines the erros of JSON-RPC 2.0.
 * @see https://www.jsonrpc.org/specification
 */
public enum ErrorEnum {
    /**
     * An error occurred on the server while parsing the JSON text.
     */
    PARSE_ERROR(     -32700, "Parse error",      "An error occurred on the server while parsing the JSON text."),
    
    /**
     * The JSON sent is not a valid Request object.
     */
    INVALID_REQUEST( -32600, "Invalid Request",  "The JSON sent is not a valid Request object."),
    
    /**
     * The method does not exist / is not available.
     */
    METHOD_NOT_FOUND(-32601, "Method not found", "The method does not exist / is not available."),
    
    /**
     * Invalid method parameter(s).
     */
    INVALID_PARAMS(  -32602, "Invalid Params",   "Invalid method parameter(s)."),
    
    /**
     * Server error
     */
    INTERNAL_ERROR(  -32603, "Internal error",   "Server error"),
    
    /**
     * Reserved for implementation-defined server-errors.
     */
    SERVER_ERROR(    -32000, "Server error",     "Reserved for implementation-defined server-errors.");

    private final int code;
    private final String message;
    private final String meaning;
    
    private ErrorEnum(int code, String message, String meaning) {
        this.code = code;
        this.message = message;
        this.meaning = meaning;
    }

    /**
     * Returns the error code.
     * @return 
     */
    public int getCode() {
        return code;
    }
    
    /**
     * Returns the error message.
     * @return 
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Returns the meaning of error.
     * @return 
     */
    public String getMeaning() {
        return meaning;
    }
}
