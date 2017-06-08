package com.xiyuan.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Created by xiyuan_fengyu on 2016/8/26.
 */
public class Md5Util {

    private static String byteArrayToHexString(byte bs[]) {
        StringBuilder resultSb = new StringBuilder();
        for (byte b: bs) {
            resultSb.append(byteToHexString(b));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String get(String origin) {
        return get(origin, StandardCharsets.UTF_8);
    }

    public static String get(String origin, Charset charsets) {
        if (charsets == null) {
            return get(origin.getBytes());
        }
        else return get(origin.getBytes(charsets));
    }

    public static String get(byte[] origin) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(origin));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }

    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

}