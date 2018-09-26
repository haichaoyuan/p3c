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
package com.alibaba.p3c.pmd.lang.java.rule.comment;

import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.java.rule.util.NodeSortUtils;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.vm.ast.ASTComment;

/**
 * [Mandatory] All enumeration type fields should be commented as Javadoc style.
 * [强制] 所有枚举类型的字段必须使用 JaveDoc 类型注释
 * @author keriezhang
 * @date 2016/12/14
 */
public class EnumConstantsMustHaveCommentRule extends AbstractAliCommentRule {
    private static final Pattern EMPTY_CONTENT_PATTERN = Pattern.compile("", Pattern.DOTALL);

    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {
        SortedMap<Integer, Node> itemsByLineNumber = this.orderedCommentsAndEnumDeclarations(cUnit);

        // Check comments between ASTEnumDeclaration and ASTEnumConstant.
        boolean isPreviousEnumDecl = false;
        boolean isReport = false;

        for (Entry<Integer, Node> entry : itemsByLineNumber.entrySet()) {
            Node value = entry.getValue();

            if(isReport){
                Node enumBody = value.jjtGetParent();
                Node enumDeclaration = enumBody.jjtGetParent();
                addViolationWithMessage(data, enumBody,
                        I18nResources.getMessage("java.comment.EnumConstantsMustHaveCommentRule.violation.msg",
                                enumDeclaration.getImage()));
                isReport = false;
            } else
            if (value instanceof ASTEnumDeclaration) {
                isPreviousEnumDecl = true;
            } else if (value instanceof ASTEnumConstant && isPreviousEnumDecl) {
                //todo 这个意思是类声明(ASTEnumDeclaration)和常量(ASTEnumConstant),中间有个其他属性，这里是注释
                Node enumBody = value.jjtGetParent();
                Node enumDeclaration = enumBody.jjtGetParent();
                addViolationWithMessage(data, enumBody,
                    I18nResources.getMessage("java.comment.EnumConstantsMustHaveCommentRule.violation.msg",
                        enumDeclaration.getImage()));
                isPreviousEnumDecl = false;
            }else if (value instanceof Comment && isPreviousEnumDecl) {
                //todo 判断注释格式，是否\/**\/
                if(value instanceof SingleLineComment || value instanceof MultiLineComment){
                    isReport = true;
                    isPreviousEnumDecl = false;
                } else {
                    isPreviousEnumDecl = false;
                }
            } else {
                isPreviousEnumDecl = false;
            }
        }

        return super.visit(cUnit, data);
    }

    private SortedMap<Integer, Node> orderedCommentsAndEnumDeclarations(ASTCompilationUnit cUnit) {
        SortedMap<Integer, Node> itemsByLineNumber = new TreeMap<>();

        List<ASTEnumDeclaration> enumDecl = cUnit.findDescendantsOfType(ASTEnumDeclaration.class);
        NodeSortUtils.addNodesToSortedMap(itemsByLineNumber, enumDecl);

        List<ASTEnumConstant> contantDecl = cUnit.findDescendantsOfType(ASTEnumConstant.class);
        NodeSortUtils.addNodesToSortedMap(itemsByLineNumber, contantDecl);

        NodeSortUtils.addNodesToSortedMap(itemsByLineNumber, cUnit.getComments());
        //todo 构建的SortedMap 的key 是用行数左移16位加上列数，保证代码属性顺序
        return itemsByLineNumber;
    }

}
