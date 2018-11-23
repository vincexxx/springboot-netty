package com.vince.server.handler;


import com.vince.protocol.device.RcvMessagePacket;
import com.vince.protocol.device.SendMessagePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandler extends SimpleChannelInboundHandler<RcvMessagePacket> {
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RcvMessagePacket rcv) throws Exception {
        SendMessagePacket echo = rcv.process();
        if (echo != null) {
            ctx.writeAndFlush(echo);
        }
    }
}
