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
package com.alibaba.p3c.pmd.lang.java.rule.naming;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import com.alibaba.p3c.pmd.lang.java.rule.util.CheckExcludeClassNameUtil;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;

/**
 * [Mandatory] All names should not start or end with an underline or a dollar sign.
 * [强制] 1. 所有名字不能使用下划线和美元符号开始或者结束
 * @author changle.lq
 * @date 2017/04/16
 */
public class AvoidStartWithDollarAndUnderLineNamingRule extends AbstractAliRule {
    private static final String DOLLAR = "$";
    private static final String UNDERSCORE = "_";
    private static final String FORMAT = I18nResources.getMessage(
        "java.naming.AvoidStartWithDollarAndUnderLineNamingRule.violation.msg");


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        super.exeExcludeByClassName(node.getImage());
        if(super.isExcludeByClassName()){
            return super.visit(node, data);
        }
        String image = node.getImage();
        if (image.startsWith(DOLLAR) || image.startsWith(UNDERSCORE)) {
            ViolationUtils.addViolationWithPrecisePosition(this, node, data, String.format(FORMAT, image));
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if(super.isExcludeByClassName()){
            return super.visit(node, data);
        }
        String image = node.getImage();
        if (image.startsWith(DOLLAR) || image.startsWith(UNDERSCORE)) {
            ViolationUtils.addViolationWithPrecisePosition(this, node, data, String.format(FORMAT, image));
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclarator node, Object data) {
        if(super.isExcludeByClassName()){
            return super.visit(node, data);
        }
        String image = node.getImage();
        if (image.startsWith(DOLLAR) || image.startsWith(UNDERSCORE)) {
            ViolationUtils.addViolationWithPrecisePosition(this, node, data, String.format(FORMAT, image));
        }
        return super.visit(node, data);
    }
}
