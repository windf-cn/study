# windf-generation
代码生成

## 要解决的问题
### 想要解决80%的重复代码的问题
其实很多时候我们都是在做重复的工作（不限于增删改查），不但花费时间，而且容易出错，很多错误都是因为不细心导致的
除去通过封装框架之外，想要提供抽象模型的方式

### 想要解决框架升级的问题
现在的代码升级太快了，Struts、Struts2、Hibernate、Mybatis、SpringBoot、SpringCloud，以至于我们不得不消耗大力气来重写代码（不是重构）
但是这个过程中，总会有不变的东西，业务逻辑，可以运行接口，经过检验的设计模式
当然了，性能优化和非功能问题也是需要琢磨的，但是解决完重复的问题，我们才更多的精力关注程序扩展性和性能以及其他非功能需求

## 核心思想
### UML
在UML上进行思考要比代码块很多，二者就像是内存和磁盘的区别
经过精细化的UML正式我们要的核心，用来生成代码的基础；
调查过一些软件，有过一些辅助的UML生成代码的，或者一些根据数据库生成的，在灵活性、系统性上，毕竟理念不一样，效果就不一样

### 模板
通过定义和了解数据模型，学习一套简单的模板规则，抽出自己的代码框架并且寻找应该被替换的点，能够让代码动态化
目前使用的是FreeMarker的形式

## 宗旨
1. 实现代码的自动生成，同时又可以方便的定制
2. 不侵入本身的代码框架，同时方便的在各个框架之间迁移
3. 能够方便的持续部署、版本控制
