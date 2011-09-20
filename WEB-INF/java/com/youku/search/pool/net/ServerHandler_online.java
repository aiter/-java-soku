package com.youku.search.pool.net;

import org.apache.mina.common.IoSession;

import com.youku.search.index.entity.Result;

/**
 * ServerHandler 的处理逻辑
 * 
 * @param <T>
 * 
 */
public class ServerHandler_online<T> extends ServerHandler_base<T> {

	/**
	 * 收到来自客户端的消息
	 */
	public void messageReceived(IoSession session, Object message)
			throws Exception {

		QueryHolder queryHolder = (QueryHolder) message;

		ResultHolder<T> resultHolder = new ResultHolder<T>();
		resultHolder.c_start = queryHolder.c_start;
		resultHolder.s_received = System.currentTimeMillis();

		try {
			resultHolder.result = (Result<T>) searchManager
					.processQuery(queryHolder.queryObject);
		} catch (Exception e) {
			resultHolder.result = ResultHolderConstant.ServerException.I;

			logger.error("lucene查询发生异常, queryObject: "
					+ queryHolder.queryObject, e);
		}

		if (resultHolder.result == null) {
			resultHolder.result = ResultHolderConstant.Null.I;

			logger.info("lucene查询返回null, queryObject: "
					+ queryHolder.queryObject);
		}

		resultHolder.s_send = System.currentTimeMillis();
		session.write(resultHolder);
	}
}
