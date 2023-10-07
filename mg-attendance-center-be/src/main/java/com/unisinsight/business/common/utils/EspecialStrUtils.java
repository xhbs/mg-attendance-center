package com.unisinsight.business.common.utils;/**
 * @desc 
 * @author  cn [cheng.nian@unisinsight.com]
 * @time    2020/9/27 21:42
 */
public class EspecialStrUtils {

    public static String change(String str) {
        return str.replace("\\", "\\\\")
                .replace("_", "\\_")
                .replace("%", "\\%");
    }
}
