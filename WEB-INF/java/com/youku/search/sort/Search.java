package com.youku.search.sort;

import org.json.JSONObject;

public interface Search {

	public JSONObject search(Parameter p) throws Exception;

	public class StringSearchResultException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private String result;

		public StringSearchResultException(String result) {
			this.result = result;
		}

		public String getResult() {
			return result;
		}
	}

}
