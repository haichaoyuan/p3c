# P3C-PMD
[toc]

## 自定义规则
### MethodMustHaveCommentRule 
1. 方法必须拥有完备注释，必须使用 /* */
2. 方法注释增加参数说明，返回值说明
3. 剔除 Override 重载的方法，去除get、set、is前缀的方法，去除Dao后缀的类，和一些特殊类(比如DaoMaster)。
## P3C-PMD插件规则
### 编程规约（一）命名风格
- 实现了1、3、4、5、6、7、8、9、14 等11条规则
- 其中可用规则(7个)：
- 1-AvoidStartWithDollarAndUnderLineNamingRule、
- 3-ClassNamingShouldBeCamelRule、
- 4-LowerCamelCaseVariableNamingRule、
- 5-ConstantFieldShouldBeUpperCaseRule、
- 6-AbstractClassShouldStartWithAbstractNamingRule、
- 7-ArrayNamingShouldHaveBracketRule、
- 9-PackageNamingRule

#### AvoidStartWithDollarAndUnderLineNamingRule
- 选用：可用
- [强制]1. [强制] 所有名字不能使用下划线和美元符号开始或者结束
- 与规则 LowerCamelCaseVariableNamingRule 冲突，**最好修改LowerCamelCaseVariableNamingRule**

#### ClassNamingShouldBeCamelRule
- 选用：可用
- 详细：[强制]3. 类型使用 UpperCamelCase 风格，一些专属名词除外（DO,BTO,VO,DAO,BO,DAOImpl,YunOs,AO,PO）
- 正则匹配 I?([A-Z][a-z0-9]+)+， 第一个可选I，然后跟着大写字母+ n 个小写或数字，如此多个。就是随便大写字母小写字母就能骗过了
- 后期可添加一些自定义专属名词

#### LowerCamelCaseVariableNamingRule
- 选用：可用
- [强制]4. 方法名，参数名，成员变量名，本地变量名必须使用 lowerCamelCase
- 正则：[a-z|$][a-z0-9]*([A-Z][a-z0-9]*)* ，小写字母或者美元符号$开头，中间跟着没有或一个或多个 小写字母或者数字，最后0个或一个或多个大写、小写、数字组合
- 使用这个正则去匹配方法名，就是方法名也可以使用$符号开头



#### ConstantFieldShouldBeUpperCaseRule
- 选用：可用
- [强制] 5. 常量命名必须全部大写，单词间使用下划线隔开。
-  优先提供白名单，比如排除 serialVersionUID




#### AbstractClassShouldStartWithAbstractNamingRule
- 选用： 可用
- [强制] 6. 抽象类名必须以Abstract 或 Base 开头
- 详细：Abstract 属性为 true, Image 属性不匹配 Abstract|Base
- 

#### ExceptionClassShouldEndWithExceptionRule
- 选用：一般
- [强制] 6. 继承Exception 的异常类必须以 Exception 结尾

#### TestClassShouldEndWithTestNamingRule
- 选用：一般
- [强制] 6. 测试用例必须以需测试的类名开始并以test结尾

#### ArrayNamingShouldHaveBracketRule
- 选用：可用
- [强制]7. 类型与中括号紧挨相连来表示数组
- 使用int[] arratDemo，而不是String argcs[] 来表示数组
- 

#### BooleanPropertyShouldNotStartWithIsRule
- 选用：一般
- [强制] 8. 不要定义布尔变量时使用 "is" 作为前缀，因为部分框架解析会引发序列化错误
- 如 isDelete 变量，则方法也是 isDelete(), 反向解析时会"误以为" 对应熟悉名称是 delete ，导致取不到
- 判断那些DO、DAO、VO、DAO 结尾的这些需要框架解析的类，里面字段类型为boolean的字段 且不能以is 开头



#### PackageNamingRule
- 选用：可用
- [强制] 9. 包名使用小写，点分隔符之间只能有一个英语单词，包名使用单数形式
- 正则意思是，小写字母或数字 加上 "." 加上 小写字母或数字

#### ServiceOrDaoClassShouldEndWithImplRule
- 选用：一般
- [强制] 14. 所有Service 和 DAO 类，必须基于SOA 原理，继承类名必须以Impl 结尾



### 编程规约（二）常量定义
- 实现了1、2等2条规则；
- 其中可用规则(2个)：
- 1-UndefineMagicConstantRule
- 2-UpperEllRule

#### UndefineMagicConstantRule
- 选用：可用
- [强制]1. 未被预先定义的魔法值，将禁止出现在代码中
- 先剔除白名单的值，再判断是否与if语句、while语句、for语句再同一行。可通过换行逃脱掉，if语句、while语句、for语句的条件写成多行
- 

#### UpperEllRule
- 选用：可用
- [强制]2. 使用long 或者 Long 类型的变量，数值后面使用大写的 'L' 替代小写的 'l', 因为小写的'l'容易跟数字1混淆
 - 逻辑是判断文字是Long 类型且以小写'l'结尾就提示。



### 编程规约（四）OOP规约
- 其中可用规则(3个)：
- 6-EqualsAvoidNullRule
- 7-WrapperTypeEqualityRule
- 18-StringConcatRule

#### EqualsAvoidNullRule
- 选用：可用
- [强制] 6. 由于调用Object 的equals 方法容易抛空指针异常，equals将由常量或者不为空的对象来调用
- 看起来很简单的规则，写的真是繁琐，没看懂。简单来说就是对象 equals 字符串，字符串放前面。但是Integer 这样的类型，就没法翻转了，还是使用前判空为佳。
- 

#### WrapperTypeEqualityRule
- 选用：可用
- [强制] 7. 包装类必须使用equals 方法而不是直接使用 "==" 符号

#### PojoMustUsePrimitiveFieldRule
- 选用：一般
- [强制]8. 所有的POJO 类必须是包装类
- 

#### PojoNoDefaultValueRule
- 选用：一般
- [强制] 9. 当定义 pojo类，类似DO、DTO、VO，不要设定任何默认值


#### PojoMustOverrideToStringRule
- 选用：一般
- [强制] 12. POJO类 必须实现 toString 方法，如果继承另一个POJO,注意在前面加一下super.toString


#### StringConcatRule
- 选用： 可用
- [推荐] 18. 当循环体内 连接多个字符串，使用 StringBuilder 的 append 方法
- 也是没看懂，





### 编程规约（五）集合处理




### 编程规约（六）并发处理




### 编程规约（七）控制语句



### 编程规约（八）注释规约
- 实现了1、2、3、4、5、6等6条规则；
- 其中可用规则(2个)：
- 5-EnumConstantsMustHaveCommentRule
- 6-RemoveCommentedCodeRule

#### CommentsMustBeJavadocFormatRule
- 选用：有全部监测注释 MethodMustHaveCommentRule，可不用
- 详细：[强制] 1. Java 类、变量、方法、字段的注释必须是 javeDoc, 而且必须 \/** comment *\/, 而不是 '// XXX  

#### AbstractMethodOrInterfaceMethodMustUseJavadocRule
- 选用：有全部监测注释 MethodMustHaveCommentRule，可不用
- 详细：[强制]2. 抽象方法(包括接口中的方法)必须用 Javadoc 注释

#### ClassMustHaveAuthorRule
- 选用：可不用，现在大多直接拷贝代码，无意义
- 详细： [强制]3. 每个类必须包含作者和日期信息其实就是检查注释，正则查找 @author, 没查找date

#### AvoidCommentBehindStatementRule
- 选用：可不用，单行注释有人习惯放后面，为了看着方便
- 详细：[强制]4.  方法的单行注释将放置在代码前，并使用 // 或使用多行 \/**\/ ,注释的对齐特别注意

#### EnumConstantsMustHaveCommentRule
- 选用：**可用**
- 详细： [强制]5. 所有枚举类型的字段必须使用 JaveDoc 类型注释  
例子：

```
public enum Level {
    /**
     * high, medium and low
     */
    HIGH, MEDIUM, LOW
}
```

#### RemoveCommentedCodeRule
- 选用：**可用** 
- 详细： [推荐]6. 双斜杠注释掉的包导入代码、字段声明、方法将删除；javeDoc 注释和三斜杠注释可被保留


## IDEA 自带规则
1. long或者Long初始赋值时，必须使用大写的L，不能是小写的l，小写容易跟数字1混淆，造成误解。
2. Map/Set的key为自定义对象时，必须重写hashCode和equals。
3. Object的equals方法容易抛空指针异常，应使用常量或确定有值的对象来调用equals。
4. 不能使用过时的类或方法。
5. 中括号是数组类型的一部分，数组定义如下
6. 后台输送给页面的变量必须加感叹号，${var}——中间加感叹号！
7. 在if/else/for/while/do语句中必须使用大括号，即使只有一行代码，避免使用下面的形式
8. 所有的包装类对象之间值的比较，全部使用equals方法比较。
9. 所有的覆写方法，必须加@Override注解
10. 避免通过一个类的对象引用访问此类的静态变量或静态方法，无谓增加编译器解析成本，直接用类名来访问即可。