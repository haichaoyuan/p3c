package com.alibaba.p3c.pmd.lang.java.rule.util;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.*;

import java.util.Arrays;
import java.util.List;

/**
 * 类名是否被排除
 */
public class CheckExcludeClassNameManager {
    /**
     * 排除类
     */
    private static final List<String> classNameExcludeList = Arrays.asList("DaoMaster", "DaoSession");

    /**
     * 指定字符串结束的类名
     */
    private static final List<String> enbClassNameExcludeList = Arrays.asList("Dao", "Table", "Test");

    /**
     * 排除注解
     */
    private static final List<String> annotationNameExcludeList = Arrays.asList("Entity");

    private boolean excludeByClassName;//排序一些不检测的类
    private String classAnnotation;//类名注解，剔除不检测的类

    public CheckExcludeClassNameManager(ASTCompilationUnit node){
        excludeByClassName = false;
        initClassNameAndAnnotation(node);
    }

    /**
     * 当前类是否被排除
     *
     * @param mClassName 类名
     */
    public void exeExcludeByClassName(String mClassName) {
        boolean tmp = CheckExcludeClassNameManager.isExcludeByClassName(mClassName, classAnnotation);
        if (tmp) {//防止单个类文件里面有多个类
            excludeByClassName = true;
        }
    }

    /**
     * @return 是否被排除，默认不排除
     */
    public boolean isExcludeByClassName() {
        return excludeByClassName;
    }

    /**
     * @param className 类名
     * @param classAnnotation 注解名
     * @return true, 被排除
     */
    public static boolean isExcludeByClassName(String className, String classAnnotation){
        // 1. 判断类名是否被排除
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
        // 2. 判断注解名是否被排除
        if (classAnnotation != null){
            for (String s : annotationNameExcludeList) {
                if (classAnnotation.equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 初始化类名和注解
     * @param node
     */
    private void initClassNameAndAnnotation(ASTCompilationUnit node) {
        classAnnotation = null;
        int i = node.jjtGetNumChildren();
        Node typeNode = node.jjtGetChild(i -1);
        if (typeNode instanceof ASTTypeDeclaration) {
            Node annoNode = typeNode.jjtGetChild(0);
            if (annoNode instanceof ASTAnnotation) {
                ASTAnnotation astAnnotation = (ASTAnnotation) annoNode;
                ASTName marker = astAnnotation.getFirstDescendantOfType(ASTName.class);
                if (marker != null) {
                    String image = marker.getImage();
                    classAnnotation = image;
                }
            }
        }
    }

}
