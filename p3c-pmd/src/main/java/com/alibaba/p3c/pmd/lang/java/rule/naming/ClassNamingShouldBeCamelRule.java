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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.alibaba.p3c.pmd.I18nResources;
import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import com.alibaba.p3c.pmd.lang.java.rule.util.CheckExcludeClassNameUtil;
import com.alibaba.p3c.pmd.lang.java.util.ViolationUtils;
import com.alibaba.p3c.pmd.lang.java.util.namelist.NameListConfig;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;

/**
 * [Mandatory] Class names should be nouns in UpperCamelCase except domain models: DO, BO, DTO, VO, etc.
 * [强制]3. 类型使用 UpperCamelCase 风格，一些专属名词除外（DO,BTO,VO,DAO,BO,DAOImpl,YunOs,AO,PO）
 * 正则匹配 I?([A-Z][a-z0-9]+)+， 第一个可选I，然后跟着大写字母+ n 个小写或数字，如此多个。就是随便大写字母小写字母就能骗过了
 *
 * @author changle.lq
 * @date 2017/04/16
 */
public class ClassNamingShouldBeCamelRule extends AbstractAliRule {

    private static final Pattern PATTERN
            = Pattern.compile("^I?([A-Z][a-z0-9]+)+(([A-Z])|(DO|DTO|VO|DAO|BO|DAOImpl|YunOS|AO|PO))?$");

    private static final List<String> CLASS_NAMING_WHITE_LIST = NameListConfig.NAME_LIST_SERVICE.getNameList(
            ClassNamingShouldBeCamelRule.class.getSimpleName(), "CLASS_NAMING_WHITE_LIST");

    /**
     * 无需监测的命名
     */
    private static final List<String> NO_CHECK_NAME = Arrays.asList("XH", "DB", "IO");


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        String image = node.getImage();
        super.exeExcludeByClassName(image);
        if (super.isExcludeByClassName()) {
            return super.visit(node, data);
        }
        for (String s : CLASS_NAMING_WHITE_LIST) {
            if (image.contains(s)) {
                return super.visit(node, data);
            }
        }
        for (String s : NO_CHECK_NAME) {
            if (image.contains(s)) {
                //特殊情况，接口 IObjectiveUserAnswerView
                if("IO".equals(s) && node.isInterface()){
                    continue;
                }
                image = image.replace(s, "");
            }
        }
        if (PATTERN.matcher(image).matches()) {
            return super.visit(node, data);
        }
        ViolationUtils.addViolationWithPrecisePosition(this, node, data,
                I18nResources.getMessage("java.naming.ClassNamingShouldBeCamelRule.violation.msg",
                        image));

        return super.visit(node, data);
    }
}
