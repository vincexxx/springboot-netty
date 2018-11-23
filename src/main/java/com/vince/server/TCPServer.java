package com.vince.server;

import com.vince.toolkit.framework.util.log.LogUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TCPServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(TCPServer.class);

    Channel channel;

    @Autowired
    private ServerBootstrap serverBootstrap;

    private TCPServer() {
    }

    public void startup() {
        try {
            channel = serverBootstrap.bind().sync().channel().closeFuture().sync().channel();
        } catch (InterruptedException e) {
            LogUtil.error(LOGGER, "start server error.", e);
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        channel.close();
        channel.parent().close();
    }

}
