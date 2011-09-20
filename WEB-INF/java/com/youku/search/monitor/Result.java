/**
 * 
 */
package com.youku.search.monitor;

/**
 * @author 1verge
 *
 */
public class Result {
	  private boolean ok  = true;
	  private String message;
	  private Exception exception;
	  
	  
	
	public boolean isOk() {
		return ok;
	}
	public void setOk(boolean ok) {
		this.ok = ok;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception e) {
		this.exception = e;
	}
	  
	  
}
