package com.vince.server.config;

import com.vince.server.ServerChannelInitializer;
import com.vince.server.handler.MessageDecoder;
import com.vince.server.handler.MessageEncoder;
import com.vince.server.handler.MessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class ServerConfiguration {

    @Value("${boss.thread.count}")
    private int bossCount;

    @Value("${worker.thread.count}")
    private int workerCount;

    @Value("${tcp.port}")
    private int port;

    @Value("${so.keepalive}")
    private boolean keepalive;

    @Value("${so.backlog}")
    private int backlog;

    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress inetSocketAddress() {
        return new InetSocketAddress(port);
    }

    @Autowired
    private ServerChannelInitializer serverChannelInitializer;

    @Bean
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .localAddress(inetSocketAddress())
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(serverChannelInitializer)
                .option(ChannelOption.SO_BACKLOG, backlog)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, keepalive)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return b;
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossCount);
    }

    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerCount);
    }

    /*@Bean(name = "messageDecoder")
    public MessageDecoder messageDecoder() {
        return new MessageDecoder();
    }

    @Bean(name = "messageEncoder")
    public MessageEncoder messageEncoder() {
        return new MessageEncoder();
    }

    @Bean(name = "messageHandler")
    public MessageHandler messageHandler() {
        return new MessageHandler();
    }*/

}
