package com.vince.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CmdType {
    public static final int CMD_1000 = 0x1000;
    public static final int CMD_ECHO_1000 = 0x2000;
    public static final int CMD_1001 = 0x1001;
    public static final int CMD_ECHO_1001 = 0x2001;
    public static final int CMD_1002 = 0x1002;
    public static final int CMD_ECHO_1002 = 0x2002;
    public static final int CMD_1003 = 0x1003;
    public static final int CMD_ECHO_1003 = 0x2003;

    private static Map<Integer, Integer> echoMap = new ConcurrentHashMap<>();

    static {
        echoMap.put(CMD_1000, CMD_ECHO_1000);
        echoMap.put(CMD_1001, CMD_ECHO_1001);
        echoMap.put(CMD_1002, CMD_ECHO_1002);
        echoMap.put(CMD_1003, CMD_ECHO_1003);
    }

    public static int getEchoCmd(int cmd) {
        return echoMap.get(cmd);
    }


}
