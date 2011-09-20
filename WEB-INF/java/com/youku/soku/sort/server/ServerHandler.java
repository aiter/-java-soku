package com.youku.soku.sort.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

import com.youku.search.index.entity.Result;
import com.youku.search.pool.net.QueryHolder;
import com.youku.search.pool.net.ResultHolder;
import com.youku.search.pool.net.ResultHolderConstant;
import com.youku.soku.Query;
import com.youku.soku.index.SearchManager;

/**
 * ServerHandler 的处理逻辑
 * 
 */
public class ServerHandler extends IoHandlerAdapter {

	Log logger = LogFactory.getLog(getClass());

	SearchManager searchManager = SearchManager.getInstance();

	/**
	 * 收到来自客户端的消息
	 */
	public void messageReceived(IoSession session, Object message)
			throws Exception {

		QueryHolder queryHolder = (QueryHolder) message;

		ResultHolder resultHolder = new ResultHolder();
		resultHolder.c_start = queryHolder.c_start;
		resultHolder.s_received = System.currentTimeMillis();

		try {
			Query query = (Query) queryHolder.queryObject;

			resultHolder.result = (Result) searchManager.processQuery(query);

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
