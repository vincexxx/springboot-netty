package com.vince.server.handler;

import com.vince.protocol.device.SendMessagePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<SendMessagePacket> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, SendMessagePacket send, ByteBuf byteBuf) throws Exception {
        byte[] encode = send.encode();
        byteBuf.writeBytes(encode);
    }
}
