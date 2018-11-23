package com.vince.util;

public class BytesDeserializer {

    public static long convertBytesToLong(byte[] hex, int start, int len, boolean reversed) {
        if (reversed) {
            return ((long) hex[start + 0] & 0xFFL) | ((long) (hex[start + 1] & 0xFFL) << 8) | ((long) (hex[start + 2] & 0xFFL) << 16) | ((long) (hex[start + 3] & 0xFFL) << 24);
        } else {
            return ((long) hex[start + 3] & 0xFFL) | ((long) (hex[start + 2] & 0xFFL) << 8) | ((long) (hex[start + 1] & 0xFFL) << 16) | ((long) (hex[start + 0] & 0xFFL) << 24);
        }
    }

    public static byte[] convertLongToBytes(byte[] input, int start, long val, boolean reversed) {
        if (reversed) {
            input[start + 0] = (byte) val;
            input[start + 1] = (byte) ((val & 0xFF00) >>> 8);
            input[start + 2] = (byte) ((val & 0xFF0000) >>> 16);
            input[start + 3] = (byte) ((val & 0xFF000000) >>> 24);
        } else {
            input[start + 3] = (byte) val;
            input[start + 2] = (byte) ((val & 0xFF00) >>> 8);
            input[start + 1] = (byte) ((val & 0xFF0000) >>> 16);
            input[start + 0] = (byte) ((val & 0xFF000000) >>> 24);
        }
        return input;
    }

    public static int convertBytesToInt(byte[] hex, int start, int len, boolean reversed) {
        if (reversed) {
            return ((int) hex[start + 0] & 0xFF) | ((int) (hex[start + 1] & 0xFF) << 8);
        } else {
            return ((int) hex[start + 1] & 0xFF) | ((int) (hex[start + 0] & 0xFF) << 8);
        }
    }

    public static byte[] convertIntToBytes(byte[] input, int start, int val, boolean reversed) {
        if (reversed) {
            input[start + 0] = (byte) val;
            input[start + 1] = (byte) ((val & 0xFF00) >>> 8);
        } else {
            input[start + 1] = (byte) val;
            input[start + 0] = (byte) ((val & 0xFF00) >>> 8);
        }
        return input;
    }

    public static String convertBytesToHexStr(byte[] val) {
        if (val == null || val.length == 0) {
            return null;
        }
        StringBuilder str = new StringBuilder();
        for (byte b : val) {
            str.append(String.format("%02x", b));
        }
        return str.toString();
    }

}
