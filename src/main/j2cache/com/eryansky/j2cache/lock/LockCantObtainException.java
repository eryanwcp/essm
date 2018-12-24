package com.eryansky.j2cache.lock;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2018-12-24
 */
public class LockCantObtainException extends RuntimeException {

    /**
     */
    private static final long serialVersionUID = 1L;

    public LockCantObtainException() {
        super();
    }

    public LockCantObtainException(String message) {
        super(message);
    }

}