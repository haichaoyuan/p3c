package com.alibaba.p3c.pmd.lang.java.rule.special;

import com.alibaba.p3c.pmd.lang.java.rule.comment.AbstractAliCommentRule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * catch 代码块必须写代码
 */
public class TryCatchRule extends AbstractAliCommentRule {
    @Override
    public Object visit(ASTCatchStatement node, Object data) {
        //1. 获取child block
        int childNum = node.jjtGetNumChildren();
        for (int i = 0; i < childNum; i++) {
            Node nodeTmp = node.jjtGetChild(i);
            if(nodeTmp instanceof ASTBlock){
                ASTBlock astBlock = (ASTBlock)nodeTmp;
                //代码数
                int blockNum = astBlock.jjtGetNumChildren();
                if(blockNum <= 0){
                    addViolation(data, node);
                }
            }
        }
        return super.visit(node, data);
    }
}
