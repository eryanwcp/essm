package com.eryansky.j2cache.lock;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author 尔演@Eryan eryanwcp@gmail.com
 * @date 2018-12-24
 */
public class LockInsideExecutedException extends RuntimeException {

    /**
	 */
    private static final long serialVersionUID = 2331579241110939344L;

    public LockInsideExecutedException() {
        super();
    }

    public LockInsideExecutedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockInsideExecutedException(Throwable cause) {
        super(cause);
    }

}