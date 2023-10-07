package com.unisinsight.business.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author wangyi01 [wang.yi@unisinsight.com]
 * @date 2021/7/12
 * @since 1.0
 */
public class MD5Utils {

    public static String md5(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());

            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String s = Integer.toHexString(0xff & b);
                if (s.length() == 1) {
                    sb.append("0").append(s);
                } else {
                    sb.append(s);
                }
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return str;
    }
}
