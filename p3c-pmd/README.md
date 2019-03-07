## 前言

版本 | 日期 | 说明
---|---|---
2.0.8 | 2018年 | 监测规则17条，其中强制修改规则共 9 条，仅提示可不修改规则 8 条。
2.0.9 | 19.03.12 | 解决一个bug，新增三条需求，新增强制性修改规则5条，仅提示可不修改规则3条



## 2.0.9 版本
### 解决 bug
1. 解决 NullPointerException at com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule.isExcludeByClassName
2. 

### 新需求
1. native 方法，方法名驼峰规则不检测(0215)
2. try catch 里面监测是否书写代码(0307)
3. Jenkins 检查出的问题提示改为中文

### 新增强制修改规则
类别 | 规则 | 备注
---|--- | ---
flowcontrol | SwitchStatementRule | 在一个 Switch 块，存在语句时需要使用 break\return 结束。
flowcontrol | NeedBraceRule | 在 if/else/for/while/do 语句中必须使用大括号。
other | AvoidPatternCompileInMethodRule | 当使用正则时，预先编译可用提升匹配性能
other | AvoidMissUseOfMathRandomRule|Math.random() 的返回类型时 double ,值范围是 [0~1)，如果需要 int 类型，最正确的方式去使用 nextInte 或者 nextLong ,而不是放大十倍取整
other | AvoidNewDateGetTimeRule |  使用 System.currentTimeMillis() 去获取当前时间，而不是使用 new Date().getTime()

### 新增提示性规则
类别 | 规则 | 备注
---|--- | ---
flowcontrol | AvoidComplexConditionRule | 不要在条件语句里面使用复杂语句(除了常用方法如，getXxx、isXX), 使用临时布尔变量去存储复杂语句的值会增加代码可读性
flowcontrol | AvoidNegationOperatorRule | 取反逻辑不好立即理解，并且取反逻辑必然存在对应的正向逻辑写法 
other | MethodTooLongRule | 方法的总代码行数最好不要超过80行。

## 2.0.8 版本
### 现阶段强制修改规则：

类别 | 规则名 | 备注
---|---|---
comment | MethodMustHaveCommentRule | 方法需要增加完备注释，参数的说明，返回值的说明都需要
comment | EnumConstantsMustHaveCommentRule | 所有的枚举类型字段必须要有注释，说明每个数据项的用途。
constant | UpperEllRule | 1l -> 1L
name | AvoidStartWithDollarAndUnderLineNamingRule | 避免美元符号，下划线开头命名
name | LowerCamelCaseVariableNamingRule | 变量，方法名使用小驼峰
name| ConstantFieldShouldBeUpperCaseRule| 常量命名应该全部大写，单词间用下划线隔开
name | AbstractClassShouldStartWithAbstractNamingRule | 抽象类命名使用Abstract或Base开头
oop | EqualsAvoidNullRule | equals 方法常量放前面
oop | WrapperTypeEqualityRule | 包装类必须使用 equal 而不是 “=”


### 现阶段提示性规则：

类别 | 规则名 | 备注
---|---|---
comment | RemoveCommentedCodeRule | 移除方法内代码双斜杠注释，对于临时注释和可能再次使用代码，可以使用 三斜杠 “///”
comment | XHCoreRule| XHCore 检查，包括微服务的使用
comment | DensityAdapterRule | 屏幕适配规则, 检查学海 XHCore 已被声明弃用的类的监测，XHDensityUtil、XHToast、TipToast；检查 Activity 或者 Application 不可重写 getResource 方法
constant | UndefineMagicConstantRule |不允许任何魔法值（即未经定义的常量）直接出现在代码中。
name| ClassNamingShouldBeCamelRule | 类名使用驼峰命名
name | ArrayNamingShouldHaveBracketRule |  数组定义：String args[] -> String[] args
name | PackageNamingRule | 包名统一使用小写，点分隔符之间有且仅有一个自然语义的英语单词。
oop | StringConcatRule | 循环体内，字符串的联接方式，使用StringBuilder的append方法
