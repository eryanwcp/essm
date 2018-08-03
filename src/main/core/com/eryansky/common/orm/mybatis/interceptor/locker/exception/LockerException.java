package com.eryansky.common.orm.mybatis.interceptor.locker.exception;

public class LockerException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -1458375495699446661L;

    public LockerException() {}

    public LockerException(String message) {
        super(message);
    }

    public LockerException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockerException(Throwable cause) {
        super(cause);
    }
}
