package com.youku.search.pool.net;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.IoFuture;
import org.apache.mina.common.IoFutureListener;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

import com.youku.search.pool.net.mina.ByteBufferUtil;
import com.youku.search.pool.net.mina.MinaProtocolCodecFactory;
import com.youku.search.pool.net.util.Cost;

public class PoolableSessionFactory implements KeyedPoolableObjectFactory {

	public static final ThreadLocal<Cost> makeCost = new ThreadLocal<Cost>();
	public static final ThreadLocal<Cost> validateCost = new ThreadLocal<Cost>();
	public static final ThreadLocal<Cost> destroyCost = new ThreadLocal<Cost>();

	private Log logger = LogFactory.getLog(getClass());

	private ThreadPoolExecutor executorIo = MinaExecutors.io();
	private ThreadPoolExecutor executorFilterChain = MinaExecutors.filter();

	private SocketConnectorConfig cfg;
	private SocketConnector connector;

	public static final long MAKE_TIME_OUT = 300;// 毫秒数

	public static final PoolableSessionFactory I = new PoolableSessionFactory();

	private PoolableSessionFactory() {

		// 设置属性
		ByteBufferUtil.initByteBuffer();

		cfg = new SocketConnectorConfig();

		cfg.setThreadModel(ThreadModel.MANUAL);

		cfg.setConnectTimeout(5);

		// cfg.getSessionConfig().setTcpNoDelay(true);

		DefaultIoFilterChainBuilder chain = cfg.getFilterChain();
		
//		chain.addLast("codec", new ProtocolCodecFilter(
//				new ObjectSerializationCodecFactory()));
		chain.addLast("codec", new ProtocolCodecFilter(
				new MinaProtocolCodecFactory(false)));

		chain.addLast("threadPool", new ExecutorFilter(executorFilterChain));

		int ioCount = Runtime.getRuntime().availableProcessors() + 1;

		connector = new SocketConnector(ioCount, executorIo);

		logger.info("mina client starts with " + ioCount + " io thread(s)");

		// 监视ThreadPoolExecutor
		final long period = 60 * 1000;
		ThreadPoolExetutorMonitor.monitor("c_filter", executorFilterChain, period);

		// 注册一个 IoSession 清理机
		// IoSessionCleanerRegistrar.register(connector);
	}

	public Object makeObject(Object key) throws Exception {
		Cost make = new Cost();
		makeCost.set(make);

		InetSocketAddress address = (InetSocketAddress) key;
		ConnectFuture future = connector.connect(address, new ClientHandler(), cfg);

		IoSession session = null;
		Throwable cause = null;

		try {
			if (future.join(MAKE_TIME_OUT)) {
				session = future.getSession();
			}
		} catch (Throwable t) {
			cause = t;
		}

		make.updateEnd();

		if (session != null) {
			return session;
		}

		// error!
		String message;
		if (cause == null) {
			message = "超时引起的borrowObject失败: " + MAKE_TIME_OUT + "/"
					+ make.getCost();

			future.addListener(new IoFutureListener() {
				public void operationComplete(IoFuture ioFuture) {
					ConnectFuture future = (ConnectFuture) ioFuture;
					if (future.isConnected()) {
						future.getSession().close();
					} else {
						try {
							future.getSession();
						} catch (Throwable t) {
							logger.error("超时引起了一个borrowObject失败，"
									+ "随后获取该socket链接也失败：" + t.getMessage(), t);
						}
					}
				}
			});

		} else {
			message = "内部异常引起的borrowObject失败: " + cause.getMessage();
		}

		throw new RuntimeException(message, cause);
	}

	public void passivateObject(Object key, Object obj) throws Exception {
	}

	public void activateObject(Object key, Object obj) throws Exception {
	}

	public boolean validateObject(Object key, Object obj) {

		Cost validate = new Cost();
		validateCost.set(validate);

		boolean valid = true;
		try {
			IoSession session = (IoSession) obj;
			if (session == null || session.isClosing()) {// 无效的对象
				valid = false;
			}
		} catch (Exception e) {// 无效的对象
			valid = false;
			logger.error(e.getMessage(), e);
		}

		validate.updateEnd();

		return valid;
	}

	public void destroyObject(Object key, Object obj) throws Exception {

		Cost destroy = new Cost();
		destroyCost.set(destroy);

		try {
			IoSession session = (IoSession) obj;
			if (session != null && !session.isClosing()) {
				session.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		destroy.updateEnd();
	}
}
