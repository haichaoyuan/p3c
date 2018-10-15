package com.alibaba.p3c.pmd.lang.java.rule.demo;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.java.rule.comment.AbstractAliCommentRule;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import org.jaxen.JaxenException;

import java.util.List;
import java.util.regex.Pattern;

public class MethodMustHaveCommentRule extends AbstractAliCommentRule {
    //错误消息的key
    private static final String MESSAGE_KEY_PREFIX
            = "java.comment.AbstractMethodOrInterfaceMethodMustUseJavadocRule.violation.msg";
    /** 正则判断 除了 /* 是否还有其他字符， （）
     * 匹配一个或多个中括号里的 '/'、'*'、 '\n\r' 换行，'\s' 空格，在匹配 有或者没有 '@ '开头后面加任意个人字符
     * 一行一匹配
     */
    private static final Pattern EMPTY_CONTENT_PATTERN = Pattern.compile("[/*\\n\\r\\s]+(@.*)?", Pattern.DOTALL);
    //xpath 方式查询 method 下面的参数
    private static final String METHOD_VARIABLE_DECLARATOR_XPATH
            = "./MethodDeclarator/FormalParameters/FormalParameter/VariableDeclaratorId";
    // @return 单词是否存在
    private static final Pattern RETURN_PATTERN = Pattern.compile(".*@return\\s+[\\u4e00-\\u9fa5_a-zA-Z0-9_]+.*", Pattern.DOTALL);




    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {
        //把注释放进方法里
        assignCommentsToDeclarations(cUnit);
        return super.visit(cUnit, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration decl, Object data) {
        //类层往下找
        List<ASTMethodDeclaration> methods = decl.findDescendantsOfType(ASTMethodDeclaration.class);
        for (ASTMethodDeclaration method : methods) {
            Comment comment = method.comment();
            if (null == comment || !(comment instanceof FormalComment)) {
                // <![CDATA[抽象方法【%s】必须使用javadoc注释]]>
                ViolationUtils.addViolationWithPrecisePosition(this, method, data,
                        I18nResources.getMessage(MESSAGE_KEY_PREFIX + ".method",
                                method.getMethodName()));
            } else {
                // /** */格式，有注释再去判断格式对不对
                this.checkMethodCommentFormat(method, data);
            }
        }
        return super.visit(decl, data);
    }

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
            ASTVariableDeclaratorId param = (ASTVariableDeclaratorId)variableDeclaratorId;
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

    public static void main(String[] arg){
//        Pattern paramNamePattern = Pattern.compile(".*@param\\s+a\\s+\\w+", Pattern.DOTALL);
        Pattern paramNamePattern = Pattern.compile(".*@param\\s+a\\s+[\\u4e00-\\u9fa5_a-zA-Z0-9_]+.*", Pattern.DOTALL);

        String a0 = "/** aaa\n" +
                "         * @param b \n" +
                "         */";
        System.out.println("0: "+ paramNamePattern.matcher(a0).matches());

        String a01 = "/** aaa\n" +
                "         * @paramba \n" +
                "         */";
        System.out.println("01: "+ paramNamePattern.matcher(a01).matches());

        String a = "/** aaa\n" +
                "         * @param a \n" +
                "         */";
        System.out.println("1: "+ paramNamePattern.matcher(a).matches());

        String a2 = "/** aaa\n" +
                "         * @param a a\n" +
                "         */";
        System.out.println("2: "+ paramNamePattern.matcher(a2).matches());
        String a3 = "/** aaa\n" +
                "         * @param a 在\n" +
                "         */";
        System.out.println("3: "+ paramNamePattern.matcher(a3).matches());

        String a4 = "/** aaa\n" +
                "         * @param a i am a boy \n" +
                "         */";
        System.out.println("4: "+ paramNamePattern.matcher(a4).matches());
    }
}
