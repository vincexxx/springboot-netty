package com.vince.server;


import com.vince.server.handler.MessageDecoder;
import com.vince.server.handler.MessageEncoder;
import com.vince.server.handler.MessageHandler;
import com.vince.server.handler.ServerHeartbeat;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("idleStateHandler",
                new IdleStateHandler(30,
                        0,
                        0,
                        TimeUnit.SECONDS));
        pipeline.addLast("idleTimeoutHandler", new ServerHeartbeat());
        pipeline.addLast("messageEncoder", new MessageEncoder());
        pipeline.addLast("messageDecoder", new MessageDecoder());
        pipeline.addLast("messageHandler", new MessageHandler());
    }
}