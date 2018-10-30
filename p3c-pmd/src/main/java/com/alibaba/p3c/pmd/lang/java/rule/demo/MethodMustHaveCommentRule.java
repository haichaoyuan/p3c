package com.alibaba.p3c.pmd.lang.java.rule.demo;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.java.rule.comment.AbstractAliCommentRule;
import com.alibaba.p3c.pmd.lang.java.rule.util.CheckExcludeClassNameUtil;
import com.alibaba.p3c.pmd.lang.java.rule.util.Utils;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.*;
import org.jaxen.JaxenException;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 方法必须要注释
 */
public class MethodMustHaveCommentRule extends AbstractAliCommentRule {
    //错误消息的key
    private static final String MESSAGE_KEY_PREFIX
            = "java.comment.AbstractMethodOrInterfaceMethodMustUseJavadocRule.violation.msg";

    /**
     * 正则判断 除了 /* 是否还有其他字符， （）
     * 匹配一个或多个中括号里的 '/'、'*'、 '\n\r' 换行，'\s' 空格，在匹配 有或者没有 '@ '开头后面加任意个人字符
     * 一行一匹配
     */
    private static final Pattern EMPTY_CONTENT_PATTERN = Pattern.compile("[/*\\n\\r\\s]+(@.*)?", Pattern.DOTALL);
    //xpath 方式查询 method 下面的参数
    private static final String METHOD_VARIABLE_DECLARATOR_XPATH
            = "./MethodDeclarator/FormalParameters/FormalParameter/VariableDeclaratorId";
    // @return 单词是否存在
    private static final Pattern RETURN_PATTERN = Pattern.compile(".*@return\\s+[\\u4e00-\\u9fa5_a-zA-Z0-9_]+.*", Pattern.DOTALL);


    private HashSet<String> mFieldList;//字段列表

    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {
        //把注释放进方法里
        assignCommentsToDeclarations(cUnit);
        return super.visit(cUnit, data);
    }



    /**
     * 获取类的所有方法，先判断注释格式是否
     *
     * @param decl 类声明
     * @param data 数据
     * @return Object
     */
    @Override
    public Object visit(ASTClassOrInterfaceDeclaration decl, Object data) {
        //[1] 去除 Dao 后缀,test后缀，Table后缀， DaoMaster, DaoSession
        if (checkClassName(decl)) return super.visit(decl, data);

        // 找到字段列表，用于 getter setter
        mFieldList = initFieldList(decl);
        //类层往下找
        List<ASTMethodDeclaration> methods = decl.findDescendantsOfType(ASTMethodDeclaration.class);
        for (ASTMethodDeclaration method : methods) {
            String methodName = method.getMethodName();
            //[2] 注解是 Override 不检测
            if (jumpWhenOverride(method)) continue;

            // [3] 方法，get前缀，set前缀，is 前缀
            if (jumpWhenGetterAndSetter(methodName, mFieldList)) continue;

            Comment comment = method.comment();
            if (null == comment || !(comment instanceof FormalComment)) {
                //[4]  <![CDATA[抽象方法【%s】必须使用javadoc注释]]>
                ViolationUtils.addViolationWithPrecisePosition(this, method, data,
                        I18nResources.getMessage(MESSAGE_KEY_PREFIX + ".method",
                                method.getMethodName()));
            } else {
                //[5]  /** */格式，有注释再去判断格式对不对
                this.checkMethodCommentFormat(method, data);
            }
        }
        return super.visit(decl, data);
    }


    /**
     * 判断是否字段的getter setter 方法
     *
     * @param methodName 方法名
     * @param fieldList 字段列表
     * @return 字段的getter setter 方法则返回 true
     */
    private boolean jumpWhenGetterAndSetter(String methodName, HashSet<String> fieldList) {
        if (methodName != null) {
            //假定的字段
            String assumeField = null;
            if (methodName.startsWith("get") || methodName.startsWith("set")) {
                assumeField = methodName.substring(3);
            }
            if (methodName.startsWith("is")) {
                assumeField = methodName.substring(2);
            }
            if(!Utils.isEmpty(assumeField) && fieldList.contains(assumeField.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    /**
     * 当方法被 Override 注解时，则跳过
     *
     * @param method 方法
     * @return 方法被 Override 注解则返回 true
     */
    private boolean jumpWhenOverride(ASTMethodDeclaration method) {
        Node methodParent = method.jjtGetParent();
        // 方法的父节点的第一个孩子 看是不是注解(ASTAnnotation)
        if (methodParent.jjtGetNumChildren() > 0 && methodParent.jjtGetChild(0) instanceof ASTAnnotation) {
            ASTAnnotation annotation = (ASTAnnotation) methodParent.jjtGetChild(0);
            //ASTAnnotation -> MarkerAnnotation -> Name
            if (annotation.jjtGetNumChildren() > 0 && annotation.jjtGetChild(0) instanceof ASTMarkerAnnotation) {
                ASTMarkerAnnotation markerAnnotation = (ASTMarkerAnnotation) annotation.jjtGetChild(0);
                if (markerAnnotation.jjtGetNumChildren() > 0 && markerAnnotation.jjtGetChild(0) instanceof ASTName) {
                    ASTName name = (ASTName) markerAnnotation.jjtGetChild(0);
                    String image = name.getImage();
                    if ("Override".equals(image)) {
                        //注解是 Override 不检测
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 类里查找所有字段
     *
     * @param decl decl
     * @return 字段列表
     */
    private HashSet<String> initFieldList(ASTClassOrInterfaceDeclaration decl) {
        List<ASTFieldDeclaration> fieldDeclarations = decl.findDescendantsOfType(ASTFieldDeclaration.class);
        HashSet<String> fieldList = new HashSet<>();//字段列表
        // List<ASTFieldDeclaration> -> ASTFieldDeclaration -> ASTVariableDeclarator -> ASTVariableDeclaratorId
        if (!Utils.isListEmpty(fieldDeclarations)) {
            for (ASTFieldDeclaration fieldDeclaration : fieldDeclarations) {
                for (int j = 0; j < fieldDeclaration.jjtGetNumChildren(); j++) {
                    if (fieldDeclaration.jjtGetChild(j) instanceof ASTVariableDeclarator) {
                        ASTVariableDeclarator astVariableDeclarator = (ASTVariableDeclarator) fieldDeclaration.jjtGetChild(j);
                        if (astVariableDeclarator.jjtGetChild(0) instanceof ASTVariableDeclaratorId) {
                            ASTVariableDeclaratorId declaratorId = (ASTVariableDeclaratorId) astVariableDeclarator.jjtGetChild(0);
                            String image = declaratorId.getImage();
                            if(!Utils.isEmpty(image)){
                                //增加一部，转换成小写，方便比较
                                fieldList.add(image.toLowerCase());
                            }
                        }
                    }
                }
            }
        }
        return fieldList;
    }

    /**
     * [1] 检查类名，去除 Dao 后缀,test后缀，Table后缀， DaoMaster, DaoSession
     *
     * @param decl decl
     * @return 无需监测，返回true
     */
    private boolean checkClassName(ASTClassOrInterfaceDeclaration decl) {
        String mClassName = decl.getImage();
        return CheckExcludeClassNameUtil.isExcludeByClassName(mClassName);
    }

    /**
     * 继续判断注释内容是否都有
     *
     * @param method
     * @param data
     */
    public void checkMethodCommentFormat(ASTMethodDeclaration method, Object data) {
        Comment comment = method.comment();
        String commentContent = comment.getImage();

        // method instruction，
        // [1] 没写注释
        if (EMPTY_CONTENT_PATTERN.matcher(commentContent).matches()) {
            // <![CDATA[请详细描述方法【%s】的功能与意图]]>
            ViolationUtils.addViolationWithPrecisePosition(this, method, data,
                    I18nResources.getMessage(MESSAGE_KEY_PREFIX + ".desc",
                            method.getMethodName()));
        }

        // description of parameters
        List<Node> variableDeclaratorIds;
        try {
            variableDeclaratorIds = method.findChildNodesWithXPath(METHOD_VARIABLE_DECLARATOR_XPATH);
        } catch (JaxenException e) {
            throw new RuntimeException(
                    "XPath expression " + METHOD_VARIABLE_DECLARATOR_XPATH + " failed: " + e.getLocalizedMessage(), e);
        }

        for (Node variableDeclaratorId : variableDeclaratorIds) {
            ASTVariableDeclaratorId param = (ASTVariableDeclaratorId) variableDeclaratorId;
            String paramName = param.getImage();
            // 参数查询 \s ：空格 ，+ ：一个或多个
            // [2] 没有参数注释,paramName 后面的 \s: 一个或多个空格， \S : 一个或多个非空格，意思就是一个空格再加上非空格(文字)
            //.*@param\s+a\s+[\u4e00-\u9fa5_a-zA-Z0-9_]+.*
            Pattern paramNamePattern = Pattern.compile(".*@param\\s+" + paramName + "\\s+[\\u4e00-\\u9fa5_a-zA-Z0-9_]+.*", Pattern.DOTALL);

            if (!paramNamePattern.matcher(commentContent).matches()) {
                ViolationUtils.addViolationWithPrecisePosition(this, method, data,
                        I18nResources.getMessage(MESSAGE_KEY_PREFIX + ".parameter",
                                method.getMethodName(), paramName));
            }
        }

        // return values
        //匹配 return 字符串哦
        if (!method.isVoid() && !RETURN_PATTERN.matcher(commentContent).matches()) {

            ViolationUtils.addViolationWithPrecisePosition(this, method, data,
                    I18nResources.getMessage(MESSAGE_KEY_PREFIX + ".return",
                            method.getMethodName()));
        }

        // possible exception
        ASTNameList nameList = method.getThrows();
        if (null != nameList) {
            List<ASTName> exceptions = nameList.findDescendantsOfType(ASTName.class);
            for (ASTName exception : exceptions) {
                String exceptionName = exception.getImage();
                Pattern exceptionPattern = Pattern.compile(".*@throws\\s+"
                        + exceptionName + ".*", Pattern.DOTALL);

                if (!exceptionPattern.matcher(commentContent).matches()) {
                    ViolationUtils.addViolationWithPrecisePosition(this, method, data,
                            I18nResources.getMessage(MESSAGE_KEY_PREFIX + ".exception",
                                    method.getMethodName(), exceptionName));
                }
            }
        }

    }

    public static void main(String[] arg) {
        PMD.main(arg);
    }
}
