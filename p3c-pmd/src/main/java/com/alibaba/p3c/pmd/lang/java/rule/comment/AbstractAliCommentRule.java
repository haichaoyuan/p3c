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

import com.alibaba.p3c.pmd.I18nResources;

import com.alibaba.p3c.pmd.fix.FixClassTypeResolver;
import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import com.alibaba.p3c.pmd.lang.java.rule.util.CheckExcludeClassNameUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.rule.comments.AbstractCommentRule;

/** ali 阿里？， 抽象注释规则
 * @author caikang
 * @date 2017/06/21
 */
public class AbstractAliCommentRule extends AbstractCommentRule {
    private boolean excludeByClassName;//排序一些不检测的类

    @Override
    public void setDescription(String description) {
        super.setDescription(I18nResources.getMessageWithExceptionHandled(description));
    }

    @Override
    public void setMessage(String message) {
        super.setMessage(I18nResources.getMessageWithExceptionHandled(message));
    }

    @Override
    public void addViolationWithMessage(Object data, Node node, String message) {
        super.addViolationWithMessage(data, node, I18nResources.getMessageWithExceptionHandled(message));
    }

    @Override
    public void addViolationWithMessage(Object data, Node node, String message, Object[] args) {
        super.addViolationWithMessage(data, node,
            String.format(I18nResources.getMessageWithExceptionHandled(message), args));
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        excludeByClassName = false;
        return super.visit(node, data);
    }

    /** 当前类是否被排除
     * @param mClassName 类名
     */
    public void exeExcludeByClassName(String mClassName){
        boolean tmp = CheckExcludeClassNameUtil.isExcludeByClassName(mClassName);
        if(tmp){//防止单个类文件里面有多个类
            excludeByClassName = true;
        }
    }

    public boolean isExcludeByClassName() {
        return excludeByClassName;
    }

    public void setExcludeByClassName(boolean excludeByClassName) {
        this.excludeByClassName = excludeByClassName;
    }
}
