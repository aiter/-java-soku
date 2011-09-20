package com.youku.search.pool.net;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.UnmappableCharacterException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoSession;

import com.youku.search.sort.util.bridge.SearchUtil;
import com.youku.search.util.Constant.QueryField;

public class ClientManager {

	public static final long DEFAULT_WAIT = 2000;
	public static final long NO_WAIT = -1;

	private Log logger = LogFactory.getLog(getClass());

	private static ClientManager self = new ClientManager();

	private ClientManager() {
	}

	public static ClientManager getInstance() {
		return self;
	}

	/**
	 * 提交一个查询
	 */
	public <T> Lock<T> query(InetSocketAddress[] addresses, Object queryObject) {
		return query(addresses, queryObject, DEFAULT_WAIT);
	}

	/**
	 * 提交一个查询
	 * 
	 * @param wait
	 *            最长查询时间
	 */
	public <T> Lock<T> query(InetSocketAddress[] addresses, Object queryObject,
			long wait) {
		return query(new LockQuery(addresses, queryObject), wait);
	}

	/**
	 * 提交一个查询
	 */
	public <T> Lock<T> query(LockQuery lockQuery) {
		return query(lockQuery, DEFAULT_WAIT);
	}

	/**
	 * 提交一个查询
	 * 
	 * @param wait
	 *            最长查询时间
	 */
	public <T> Lock<T> query(LockQuery lockQuery, long wait) {

		Lock<T> lock = new Lock<T>(lockQuery.queryObject(),
				lockQuery.addresses, wait);

		IoSession[] sessions = new IoSession[lockQuery.addresses.length];
		
		// 区分用对象序列化还是用其它编码方式
		// 给luceneServer发送的值都用对象序列化，给C-Server发送用其它
		boolean useObjectSerializeEncoder = (SearchUtil.getIsCServer(lockQuery.type) ? false : true);
		
		for (int i = 0; i < lockQuery.addresses.length; i++) {
			InetSocketAddress address = lockQuery.addresses[i];

			lock.conn_starts[i] = System.currentTimeMillis();
			sessions[i] = Pool.getConnection(address);
			lock.conn_ends[i] = System.currentTimeMillis();
		}

		lock.send = System.currentTimeMillis();
		lockQuery.queryHolder.c_start = lock.start;

		for (int i = 0; i < lockQuery.addresses.length; i++) {

			InetSocketAddress address = lockQuery.addresses[i];
			IoSession session = sessions[i];
			
			if (session == null) {
				lock.addResult(address, ResultHolderConstant.NotSent.I);
				continue;
			}
			
			try {
				session.setAttribute(Lock.KEY, lock);
				if (useObjectSerializeEncoder) {
					session.write(lockQuery.queryHolder);
				} else {
					session.write(lockQuery.queryHolder.queryObject);
				}

			} catch (Throwable e) {
				lock.addResult(address, ResultHolderConstant.NotSent.I);
				Pool.closeAndRelease(session); // 销毁链接

				if(e instanceof UnmappableCharacterException) {
					logger.error("查询发生异常！ Remote: " + address + ", message: " + e.getMessage());
				} else {
					logger.error("查询发生异常！ Remote: " + address, e);
				}
				
			}
		}
		lock.sent = System.currentTimeMillis();

		waitNofity(lock, wait);
		lock.end = System.currentTimeMillis();

		// close and release
		for (IoSession session : sessions) {
			if (session == null) {
				continue;
			}

			SocketAddress address = session.getRemoteAddress();
			if (lock.map.containsKey(address)) {
				continue;
			}

			final long cost = (lock.end - lock.start);
			logger.error("关闭查询超时的连接：" + cost + "; " + session);
			Pool.closeAndRelease(session);
		}

		return lock;
	}

	private <T> void waitNofity(Lock<T> lock, long wait) {
		synchronized (lock.sync) {
			try {
				lock.sync.wait(wait);
			} catch (Throwable t) {
				logger.error("收集查询结果发生异常！", t);
			}
		}
	}

}
