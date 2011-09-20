package com.youku.search.pool.net;

import com.youku.search.index.entity.Result;

public class ResultHolderConstant {

	public static <T> boolean isPrivateResult(Result<T> result) {
		return DummyResult.class.isAssignableFrom(result.getClass());
	}

	public static abstract class DummyResult<T> extends Result<T> {
		private static final long serialVersionUID = 1L;

		private DummyResult() {
			totalCount = 0;
			results = null;
			hasNext = false;
		}
	}

	/**
	 * 没有发出去的请求
	 * 
	 * @param <T>
	 */
	public static final class NotSent<T> extends DummyResult<T> {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public static final NotSent I = new NotSent();

		private NotSent() {
			timecost = -1;
		}
	}

	/**
	 * 没有收到请求
	 * 
	 * @param <T>
	 */
	public static final class Miss<T> extends DummyResult<T> {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public static final Miss I = new Miss();

		private Miss() {
			timecost = -2;
		}
	}

	/**
	 * 收到了空结果
	 * 
	 * @param <T>
	 */
	public static final class Null<T> extends DummyResult<T> {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public static final Null I = new Null();

		private Null() {
			timecost = -3;
		}
	}

	/**
	 * 收到超时结果
	 */
	public static final class LuceneQueryTimeout<T> extends DummyResult<T> {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public static final LuceneQueryTimeout I = new LuceneQueryTimeout();

		private LuceneQueryTimeout() {
			timecost = -4;
		}
	}

	/**
	 * client发生异常
	 */
	public static final class ClientException<T> extends DummyResult<T> {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public static final ClientException I = new ClientException();

		private ClientException() {
			timecost = -5;
		}
	}

	/**
	 * server发生异常
	 */
	public static final class ServerException<T> extends DummyResult<T> {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public static final ServerException I = new ServerException();

		private ServerException() {
			timecost = -6;
		}
	}
}