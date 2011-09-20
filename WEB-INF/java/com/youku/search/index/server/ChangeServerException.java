package com.youku.search.index.server;

/**
 * 切服过程中失败则抛出此异常
 * 
 * @author gaosong
 * 
 */
public class ChangeServerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4084225789350595255L;

	public ChangeServerException() {
		super();
	}

	public ChangeServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ChangeServerException(String message) {
		super(message);
	}

	public ChangeServerException(Throwable cause) {
		super(cause);
	}

}
