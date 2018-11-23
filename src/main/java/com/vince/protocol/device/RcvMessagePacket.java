package com.vince.protocol.device;

import com.vince.protocol.CmdType;
import com.vince.protocol.Decodable;
import com.vince.protocol.MessagePacket;

public class RcvMessagePacket extends MessagePacket implements Decodable {

    private byte[] data;

    public RcvMessagePacket(byte[] input) {
        decode(input);
    }


    public SendMessagePacket process() {
        //如果有需要，也可以在这里将数据通过mq之类发送到后台处理
        SendMessagePacket send = new SendMessagePacket();
        send.setDeviceNo(getDeviceNo());
        send.setCmd(CmdType.getEchoCmd(getCmd()));
        send.setDataLen(4);
        send.setStatus(0);
        return send;
    }

    @Override
    public void decode(byte[] input) {
        setDeviceNo(deserializeDeviceNo(input));
        setCmd(deserializeCmd(input));
        setDataLen(deserializeDataLen(input));
        data = new byte[getDataLen()];
        System.arraycopy(input, MessagePacket.HEADER_INDEX_DATA, data, 0, getDataLen());
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}