/*
 * Copyright 1999-2017 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.p3c.pmd.lang.java.rule.concurrent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.Token;

/**
 * [Mandatory] A thread pool should be created by ThreadPoolExecutor rather than Executors.
 * These would make the parameters of the thread pool understandable.
 * It would also reduce the risk of running out of system resource.
 * [强制]4. 一个线程池将被 ThreadPoolExecutor 创建，而不是 Executors。
 * ，也将减少跑光系统资源的风险。
 * 说明：Executors 返回的线程池对象的弊端如下：
 * 1). FixedThreadPool 和 SingleThreadPool. 允许请求队列长度为 Integer.MAX_VALUE, 可能会堆积大量的请求，从而导致OOM
 * 2). CachedThreadPool 和 ScheduledThreadPool. 允许创建的线程数量为 Integer.MAX_VAULE, 可能会创建大量的线程，从而导致OOM
 * @author caikang
 * @date 2016/11/14
 */
public class ThreadPoolCreationRule extends AbstractAliRule {

    private static final String DOT = ".";
    private static final String COLON = ";";
    private static final String NEW = "new";
    private static final String EXECUTORS_NEW = Executors.class.getSimpleName() + DOT + NEW;
    private static final String FULL_EXECUTORS_NEW = Executors.class.getName() + DOT + NEW;
    private static final String BRACKETS = "()";

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        Object superResult = super.visit(node, data);

        Info info = new Info();
        List<ASTImportDeclaration> importDeclarations = node.findChildrenOfType(ASTImportDeclaration.class);
        for (ASTImportDeclaration importDeclaration : importDeclarations) {
            ASTName name = importDeclaration.getFirstChildOfType(ASTName.class);
            info.executorsUsed = info.executorsUsed
                || (name.getType() == Executors.class || Executors.class.getName().equals(name.getImage()));
            if (name.getImage().startsWith(Executors.class.getName() + DOT)) {
                info.importedExecutorsMethods.add(name.getImage());
            }
        }
        List<ASTPrimaryExpression> primaryExpressions = node.findDescendantsOfType(ASTPrimaryExpression.class);
        for(ASTPrimaryExpression primaryExpression : primaryExpressions){
            if (!info.executorsUsed && info.importedExecutorsMethods.isEmpty()) {
                continue;
            }

            Token initToken = (Token)primaryExpression.jjtGetFirstToken();
            if (!checkInitStatement(initToken, info)) {
                addViolationWithMessage(data, primaryExpression,"java.concurrent.ThreadPoolCreationRule.violation.msg");
            }
        }
        return superResult;
    }

    private boolean checkInitStatement(Token token, Info info) {
        String fullAssignStatement = getFullAssignStatement(token);
        if (fullAssignStatement.startsWith(EXECUTORS_NEW)) {
            return false;
        }
        if (!fullAssignStatement.startsWith(NEW) && !fullAssignStatement.startsWith(FULL_EXECUTORS_NEW)) {
            return true;
        }
        // code with lambda
        int index = fullAssignStatement.indexOf(BRACKETS);
        if (index == -1) {
            return true;
        }
        fullAssignStatement = fullAssignStatement.substring(0, index);
        // java.util.concurrent.Executors.newxxxx
        if (info.importedExecutorsMethods.contains(fullAssignStatement)) {
            return false;
        }
        // static import
        return !info.importedExecutorsMethods.contains(Executors.class.getName() + DOT + fullAssignStatement);
    }

    private String getFullAssignStatement(final Token token) {
        if (token == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(48);
        Token next = token;
        while (next.next != null && !COLON.equals(next.image)) {
            sb.append(next.image);
            next = next.next;
        }
        return sb.toString();
    }

    class Info {
        boolean executorsUsed;
        Set<String> importedExecutorsMethods = new HashSet<>();
    }
}
