package com.youku.search.pool.net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

import com.youku.search.index.SearchManager;

/**
 * ServerHandler 的处理逻辑
 * 
 */
public class ServerHandler_base<T> extends IoHandlerAdapter {

    protected final Log logger = LogFactory.getLog(getClass());

    protected final SearchManager searchManager = SearchManager.getInstance();

    /**
     * 当有异常发生时触发
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        logger.error("exception caught: " + session, cause);
    }

    /**
     * 有新连接时触发
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
    }

    /**
     * 连接被关闭时触发
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
    }

}
