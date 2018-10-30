package com.alibaba.p3c.pmd.lang.java.rule.util;

import java.util.Arrays;
import java.util.List;

/**
 * 类名是否被排除
 */
public class CheckExcludeClassNameUtil {
    /**
     * 排除类
     */
    private static List<String> classNameExcludeList = Arrays.asList("DaoMaster", "DaoSession");

    /**
     * 指定字符串结束的类名
     */
    private static List<String> enbClassNameExcludeList = Arrays.asList("Dao", "Table", "Test");

    /**
     * @param className 类名
     * @return true, 被排除
     */
    public static boolean isExcludeByClassName(String className){
        if (className != null) {
            for (String s : classNameExcludeList) {
                if (className.equals(s)) {
                    return true;
                }
            }
            for (String s : enbClassNameExcludeList) {
                if (className.endsWith(s)) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }
}
