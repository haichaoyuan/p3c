package com.alibaba.p3c.pmd.lang.java.rule.util;

import java.util.List;

/**
 * 放置一些自身的工具类
 */
public class Utils {
    /**
     * 为空则为 true
     *
     * @param list 列表
     * @return 为空则为 true
     */
    public static boolean isListEmpty(List list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 为空则为 true
     *
     * @param s 字符串
     * @return 为空则为 true
     */
    public static boolean isEmpty(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        return false;
    }
}
