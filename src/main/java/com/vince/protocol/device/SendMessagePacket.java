package com.vince.protocol.device;

import com.vince.protocol.Decodable;
import com.vince.protocol.Encodable;
import com.vince.protocol.MessagePacket;
import com.vince.util.BytesDeserializer;

public class SendMessagePacket extends MessagePacket implements Encodable {

    private long status;

    @Override
    public byte[] encode() {
        //4为status的长度
        byte[] out = new byte[MessagePacket.HEADER_SIZE + 4];
        BytesDeserializer.convertLongToBytes(out, MessagePacket.HEADER_INDEX_DEVICE_NO, getDeviceNo(), true);
        BytesDeserializer.convertIntToBytes(out, MessagePacket.HEADER_INDEX_CMD, getCmd(), true);
        BytesDeserializer.convertIntToBytes(out, MessagePacket.HEADER_INDEX_DATA_LEN, getDataLen(), true);
        BytesDeserializer.convertLongToBytes(out, MessagePacket.HEADER_INDEX_DATA, getStatus(), true);
        return out;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }
}