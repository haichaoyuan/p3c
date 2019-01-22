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

import java.util.regex.Pattern;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;

import net.sourceforge.pmd.lang.java.ast.*;

/**
 * [Mandatory] Method names, parameter names, member variable names, and local variable names should be written in
 * lowerCamelCase.
 * [强制]4. 方法名，参数名，成员变量名，本地变量名必须使用 lowerCamelCase
 * 正则：[a-z|$][a-z0-9]*([A-Z][a-z0-9]*)* ，小写字母或者美元符号$开头，中间跟着没有或一个或多个 小写字母或者数字，最后0个或一个或多个大写、小写、数字组合
 * 使用这个正则去匹配方法名，就是方法名也可以使用$符号开头
 *
 * @author changle.lq
 * @date 2017/04/16
 */
public class LowerCamelCaseVariableNamingRule extends AbstractAliRule {

    private static final String MESSAGE_KEY_PREFIX = "java.naming.LowerCamelCaseVariableNamingRule.violation.msg";
    private Pattern pattern = Pattern.compile("^[a-z|$][a-z0-9]*([A-Z][a-z0-9]*)*(DO|DTO|VO|DAO)?$");

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        super.exeExcludeByClassName(node.getImage());
        return super.visit(node, data);
    }


    /**
     * 变量
     *
     * @param node
     * @param data
     * @return
     */
    @Override
    public Object visit(final ASTVariableDeclaratorId node, Object data) {
        if (super.isExcludeByClassName()) {
            return super.visit(node, data);
        }
        // static 、final 修饰，不检查
        // 接口里的变量也是 static 、final 的
        ASTFieldDeclaration astFieldDeclaration = node.getFirstParentOfType(ASTFieldDeclaration.class);
        if(astFieldDeclaration != null){
            //全局变量
            boolean isNotCheck = astFieldDeclaration.isFinal() || astFieldDeclaration
                    .isStatic();
            if (isNotCheck) {
                return super.visit(node, data);
            }

            //剔除 @Interface ,ASTFieldDeclaration 的父亲类型是ASTAnnotationTypeDeclaration
            //AnnotationTypeDeclaration/AnnotationTypeBody/AnnotationTypeMemberDeclaration/FieldDeclaration/VariableDeclarator/VariableDeclaratorId
            ASTAnnotationTypeMemberDeclaration astAnnotationTypeMemberDeclaration = astFieldDeclaration.getFirstParentOfType(ASTAnnotationTypeMemberDeclaration.class);
            if(astAnnotationTypeMemberDeclaration != null){
                return super.visit(node, data);
            }
        }


        // variable naming violate lowerCamelCase
        // violate 违反、侵害、妨碍、亵渎
        if (!(pattern.matcher(node.getImage()).matches())) {
            ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                    I18nResources.getMessage(MESSAGE_KEY_PREFIX + ".variable", node.getImage()));
        }
        return super.visit(node, data);
    }

    /**
     * 方法
     *
     * @param node
     * @param data
     * @return
     */
    @Override

    public Object visit(ASTMethodDeclarator node, Object data) {
        if (super.isExcludeByClassName()) {
            return super.visit(node, data);
        }
        if (!(pattern.matcher(node.getImage()).matches())) {
            ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                    I18nResources.getMessage(MESSAGE_KEY_PREFIX + ".method", node.getImage()));
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        //对所有注解内的内容不做检查
        return null;
    }
}
