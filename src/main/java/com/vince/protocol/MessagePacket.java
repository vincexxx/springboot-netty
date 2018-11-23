package com.vince.protocol;

import com.vince.util.BytesDeserializer;

public abstract class MessagePacket {

    public static final int HEADER_SIZE = 8;//协议包头总长度
    public static final int HEADER_INDEX_DEVICE_NO = 0;//包头设备编号开始下标
    public static final int HEADER_INDEX_CMD = 4;//包头CMD命令字开始下标
    public static final int HEADER_INDEX_DATA_LEN = 6;//包头关于包体的长度开始下标
    public static final int HEADER_INDEX_DATA = 8;//包体的开始下标

    private long deviceNo;//约定long占4个字节
    private int cmd;//约定int占两个字节
    private int dataLen;

    public static long deserializeDeviceNo(byte[] input) {
        return BytesDeserializer.convertBytesToLong(input, HEADER_INDEX_DEVICE_NO, 4, true);
    }

    public static int deserializeCmd(byte[] input) {
        return BytesDeserializer.convertBytesToInt(input, HEADER_INDEX_CMD, 2, true);
    }

    public static int deserializeDataLen(byte[] input) {
        return BytesDeserializer.convertBytesToInt(input, HEADER_INDEX_DATA_LEN, 2, true);
    }

    public long getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(long deviceNo) {
        this.deviceNo = deviceNo;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getDataLen() {
        return dataLen;
    }

    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
    }
}
