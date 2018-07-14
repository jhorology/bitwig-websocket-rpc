package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

/**
 *  https://www.jsonrpc.org/specification
 */
public enum ErrorEnum {
    PARSE_ERROR(     -32700, "Parse Error",      "An error occurred on the server while parsing the JSON text."),
    INVALID_REQUEST( -32600, "Invalid Request",  "The JSON sent is not a valid Request object."),
    METHOD_NOT_FOUND(-32601, "Method not found", "The method does not exist / is not available."),
    INVALID_PARAMS(  -32602, "Invalid Params",   "nvalid method parameter(s)."),
    INTERNAL_ERROR(  -32603, "Internal error",   "Server error"),
    SERVER_ERROR(    -32000, "Server error",     "Reserved for implementation-defined server-errors.");

    private final int code;
    private final String message;
    private final String meaning;
    
    private ErrorEnum(int code, String message, String meaning) {
        this.code = code;
        this.message = message;
        this.meaning = meaning;
    }

    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getMeaning() {
        return meaning;
    }
}
