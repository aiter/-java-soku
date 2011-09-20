package com.youku.search.pool.net;

import java.net.SocketAddress;
import java.nio.charset.UnmappableCharacterException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

public class ClientHandler<T> extends IoHandlerAdapter {

	private Log logger = LogFactory.getLog(getClass());

	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {

		Throwable causeException = cause.getCause();
		if (null != causeException && causeException instanceof UnmappableCharacterException) {
			logger.error("exception caught: [remote: " + session.getRemoteAddress()
					+ "], message=" + cause.getMessage());
		} else {
			logger.error("exception caught: [remote: " + session.getRemoteAddress()
					+ "]", cause);	
		}

		Lock<T> lock = (Lock<T>) session.getAttribute(Lock.KEY);
		SocketAddress remote = session.getRemoteAddress();

		if (lock != null && !lock.map.containsKey(remote)) {

			boolean added = lock.addResult(remote,
					ResultHolderConstant.ClientException.I);

			if (added) {
				Pool.release(session);
			}
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		
		Lock<T> lock = (Lock<T>) session.getAttribute(Lock.KEY);
		
		ResultHolder<T> resultHolder = (ResultHolder<T>) message;
		resultHolder.c_start = lock.start;
		resultHolder.c_received = System.currentTimeMillis();
		
		boolean added = lock
				.addResult(session.getRemoteAddress(), resultHolder);

		if (added) {
			Pool.release(session);
		}
	}
}
