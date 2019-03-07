package com.alibaba.p3c.pmd.lang.java.rule.special;

import com.alibaba.p3c.pmd.lang.java.rule.comment.AbstractAliCommentRule;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;

/**
 * 屏幕适配规则
 * 1. 学海 XHCore 已被声明弃用的类的监测，XHDensityUtil、XHToast、TipToast
 * 2. 检查 Activity 后缀或者 Application 后缀 不可重写 getResource
 * 3.
 */
public class DensityAdapterRule extends AbstractAliCommentRule {
    /**
     * 忽略的注解
     */
    private static final String[] checkDeprecatedClass = {"com.xh.xhcore.common.util.XHDensityUtil", "com.xh.xhcore.common.util.XHToast",
            "com.xh.view.TipToast"};


    public DensityAdapterRule() {
        super();
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        return super.visit(node, data);
    }


    /**
     * 学海 XHCore 已被声明弃用的类的监测，XHDensityUtil、XHToast、TipToast
     *
     * @param node node
     * @param data date
     * @return super.visit
     */
    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        String packageName = node.getImportedName();
        if (packageName == null) {
            return super.visit(node, data);
        }
        for (int i = 0; i < checkDeprecatedClass.length; i++) {
            String checkDeprecatedClass = DensityAdapterRule.checkDeprecatedClass[i];
            if (packageName.equals(checkDeprecatedClass)) {
                addViolationWithMessage(data, node, checkDeprecatedClass + " 已被弃用，不推荐使用");
            }
        }
        return super.visit(node, data);
    }

    /**
     * 只检查 Activity 后缀或者 Application 后缀
     */
    private boolean isCheckGetResource = false;

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        String image = node.getImage();
        if (image != null && (image.endsWith("Activity") || image.endsWith("Application"))) {
            //Activity 后缀或者 Application 后缀，检查
            isCheckGetResource = true;
        } else {
            isCheckGetResource = false;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclarator node, Object data) {
        if (!isCheckGetResource) {
            return super.visit(node, data);
        }
        String image = node.getImage();
        if(image.equals("getResources")){
            addViolationWithMessage(data, node, image + " 禁止被重写");
        }
        return super.visit(node, data);
    }
}
