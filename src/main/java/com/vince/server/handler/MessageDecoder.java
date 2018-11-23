package com.vince.server.handler;

import com.vince.protocol.MessagePacket;
import com.vince.protocol.device.RcvMessagePacket;
import com.vince.server.mapping.MessageChannelMapping;
import com.vince.toolkit.base.exception.BusinessException;
import com.vince.toolkit.framework.util.log.LogUtil;
import com.vince.util.BytesDeserializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageDecoder.class);

    private final static int MAX_SECOND_PER_PACKET = 10000;//每包最大等待时间，毫秒，超过该时间则丢包
    private final static int MAX_PACKET_LEN = 1024;//每包最大长度

    private int dataLen;
    private int readableLen;
    private RcvMessagePacket rcv;
    private long timespan = 0;

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable()) {
            return;
        }
        byte[] header = new byte[MessagePacket.HEADER_SIZE];
        byte[] body;
        readableLen = in.readableBytes();
        //如果可读长度大于等于包头的长度，就读取包头
        while (readableLen >= MessagePacket.HEADER_SIZE) {
            //标记读索引位置
            in.markReaderIndex();
            in.readBytes(header);
            readableLen -= MessagePacket.HEADER_SIZE;
            //从包头解析协议data长度
            dataLen = MessagePacket.deserializeDataLen(header);
            //校验数据包长度
            switch (checkDataLength(in, header)) {
                case CONTINUE:
                    continue;
                case RETURN:
                    return;
            }
            if (dataLen <= readableLen) {
                body = new byte[dataLen];
                in.readBytes(body);
                readableLen -= dataLen;
                int cmdData = MessagePacket.deserializeCmd(header);
                long deviceNo = MessagePacket.deserializeDeviceNo(header);
                byte[] packet = buildPacket(header, body);
                LogUtil.info(LOGGER, "接收到正确的协议,DEVICE_NO[{}],CMD[{}],[{}]",
                        deviceNo, Integer.toHexString(cmdData),
                        BytesDeserializer.convertBytesToHexStr(packet));
                mappingChannel(context, deviceNo);
                rcv = new RcvMessagePacket(packet);
                out.add(rcv);
            }
        }
        timespan = 0;
    }

    //保存设备和channel的映射关系
    private void mappingChannel(ChannelHandlerContext context, long deviceNo) {
        Channel c = MessageChannelMapping.INSTANCE.getChannel(deviceNo);
        if (c != null && c.isActive()) {
            throw new BusinessException("设备编号[%d]已存在链接[%s],该链接[%s]请求异常", deviceNo, c.id().asLongText(), context.channel().id().asLongText());
        }
        MessageChannelMapping.INSTANCE.putMapping(deviceNo, context.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        MessageChannelMapping.INSTANCE.removeChannel(ctx.channel());
        LogUtil.info(LOGGER, "CHANNEL_INACTIVE,channel[{}]", ctx.channel().id().asLongText());
        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel c = ctx.channel();
        LogUtil.error(LOGGER, cause.getMessage());
        if (c.isOpen()) {
            c.close();
        }
        super.exceptionCaught(ctx, cause);
    }

    private static byte[] buildPacket(byte[] header, byte[] body) {
        byte[] pkg = new byte[header.length + body.length];
        System.arraycopy(header, 0, pkg, 0, header.length);
        System.arraycopy(body, 0, pkg, header.length, body.length);
        return pkg;
    }

    private State checkDataLength(ByteBuf in, byte[] header) {
        byte[] err;
        if (dataLen > MAX_PACKET_LEN) {
            err = new byte[dataLen];
            in.readBytes(err);
            LogUtil.error(LOGGER,
                    "数据异常，解析到的数据长度[{}]超过包最大长度[{}],包头:[{}],错误包体[{}]",
                    dataLen, MAX_PACKET_LEN,
                    BytesDeserializer.convertBytesToHexStr(header),
                    BytesDeserializer.convertBytesToHexStr(err));
            return State.RETURN;
        }
        if (dataLen > readableLen) {
            long now = System.currentTimeMillis();
            if (timespan == 0) {
                timespan = now;
            }
            if (now - timespan < MAX_SECOND_PER_PACKET) {
                //未到超时时间
                LogUtil.warn(LOGGER,
                        "数据异常，解析到的数据长度[{}]超过缓冲区长度[{}],timespan[{}],包头:[{}]",
                        dataLen, readableLen, (now - timespan),
                        BytesDeserializer.convertBytesToHexStr(header));
                in.resetReaderIndex();
            } else {
                err = new byte[readableLen];
                in.readBytes(err);
                LogUtil.error(LOGGER,
                        "数据异常，解析到的数据长度[{}]超过缓冲区长度[{}],timespan[{}],包头:[{}],错误包体[{}]",
                        dataLen, readableLen, (now - timespan),
                        BytesDeserializer.convertBytesToHexStr(header),
                        BytesDeserializer.convertBytesToHexStr(err));
                in.resetReaderIndex();
            }
            return State.RETURN;
        }
        return State.NORMAL;
    }

    enum State {
        NORMAL,
        CONTINUE,
        RETURN;
    }

}
