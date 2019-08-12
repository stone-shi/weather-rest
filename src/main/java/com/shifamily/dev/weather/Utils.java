package com.shifamily.dev.weather;

public class Utils {
    public static int crc16(final char[] buffer, int offset, int len) {
        int crc = 0xFFFF;

        for (int j = offset; j < len ; j++) {
            crc = ((crc  >>> 8) | (crc  << 8) )& 0xffff;
            crc ^= (buffer[j] & 0xff);//byte to int, trunc sign
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xFF) << 5) & 0xffff;
        }
        crc &= 0xffff;
        return crc;

    }
}
