package com.ahdi.lib.utils.security;

public class ClientException extends RuntimeException {
    public static final int EX_INVALID_REQ = 100;
    public static final int EX_INVALID_RESP = 101;

    private static final long serialVersionUID = 1L;
    private int errCode;

    public ClientException(int errCode) {
        super();
        this.errCode = errCode;
    }

    public ClientException(int errCode, String message, Throwable cause) {
        super(message, cause);
        this.errCode = errCode;
    }

    public ClientException(int errCode, String message) {
        super(message);
        this.errCode = errCode;
    }

    public ClientException(int errCode, Throwable cause) {
        super(cause);
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }

    @Override
    public String toString() {
        String s = getClass().getName() + ": errCode[" + this.errCode + "]";
        String message = getLocalizedMessage();
        return (message != null) ? (s + ", " + message) : s;
    }
}