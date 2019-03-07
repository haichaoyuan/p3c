package com.alibaba.p3c.pmd.lang.java.rule.special;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class SpecialRulesTest extends SimpleAggregatorTst {
    private static final String RULESET = "java-xh-special";

    @Override
    public void setUp() {
        //[1] RULESET 得正确，RuleSetReferenceId 方法 里根据名字查找
        //[2] ruleName 得正确，getRuleByName 方法根据字符串比较来查找
        addRule(RULESET, "WhileLoopsMustUseBracesRule");


        //[1]  需要规则测试文件了。
        addRule(RULESET, "MethodMustHaveCommentRule");
//        //[2] XHCore 的规则
        addRule(RULESET, "XHCoreRule");
//        //[3] DensityAdapterRule 的规则
        addRule(RULESET, "DensityAdapterRule");
        //[4] TryCatchRule 的规则
        addRule(RULESET, "TryCatchRule");
        super.setUp();
    }


}
