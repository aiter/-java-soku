package com.youku.search.index.server;

public class ServerStateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 332203127568153315L;

	public ServerStateException() {
		super();
	}

	public ServerStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerStateException(String message) {
		super(message);
	}

	public ServerStateException(Throwable cause) {
		super(cause);
	}

}
