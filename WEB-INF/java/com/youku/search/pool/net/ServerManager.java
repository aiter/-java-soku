package com.youku.search.pool.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import com.youku.search.pool.net.mina.ByteBufferUtil;

public class ServerManager {

    private static Log logger = LogFactory.getLog(ServerManager.class);

    private ThreadPoolExecutor executorIo = MinaExecutors.io();
    private ThreadPoolExecutor executorFilterChain = MinaExecutors.filter();

    public static final int PORT = 8080;

    private static ServerManager self = new ServerManager();

    private ServerManager() {
        init();
    }

    public static ServerManager getInstance() {
        return self;
    }

    private void init() {

    	ByteBufferUtil.initByteBuffer();

        SocketAcceptorConfig config = new SocketAcceptorConfig();

        config.setThreadModel(ThreadModel.MANUAL);

        // config.getSessionConfig().setTcpNoDelay(true);

        DefaultIoFilterChainBuilder chain = config.getFilterChain();

        chain.addLast("codec", new ProtocolCodecFilter(
                new ObjectSerializationCodecFactory()));

        chain.addLast("threadPool", new ExecutorFilter(executorFilterChain));

        int ioCount = Runtime.getRuntime().availableProcessors() + 1;

        SocketAcceptor acceptor = new SocketAcceptor(ioCount, executorIo);

        // 启动 ServerHandler
        try {
            acceptor.bind(new InetSocketAddress(PORT), new ServerHandler(),
                    config);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("mina server starts on port " + PORT + ", with " + ioCount
                + " io thread(s)");

        // 监视ThreadPoolExecutor
        final long period = 60 * 1000;
        ThreadPoolExetutorMonitor.monitor("s_filter", executorFilterChain,
                period);

        // 注册一个 IoSession 清理机
        // IoSessionCleanerRegistrar.register(acceptor);
    }

    public static void main(String[] args) throws IOException {
        ServerManager.getInstance();
    }
}
