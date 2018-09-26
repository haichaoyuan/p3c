package com.alibaba.p3c.pmd.lang.java.rule.demo;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class WhileLoopsMustUseBracesRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {
        return super.visit(cUnit, data);
    }

    /**
     * WhileStatement 节点下不存在 Statement-Block 这个结构
     *
     * @param node
     * @param data
     * @return
     */
    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        //node.jjtGetChild(0) 是一个 Expression ，条件，就是while  后面的（）
        Node firstStmt = node.jjtGetChild(1);
        if (!hasBlockAsFirstChild(firstStmt)) {
            //记录触发规则的节点数据
            addViolation(data, node);
        }
        return super.visit(node, data);
    }

    private boolean hasBlockAsFirstChild(Node node) {
        int numChildren = node.jjtGetNumChildren();
        //自身是  ASTStatement， 有孩子， 孩子的第一个节点是 ASTBlock
        return node instanceof ASTStatement && numChildren != 0 && node.jjtGetChild(0) instanceof ASTBlock;
    }
}
