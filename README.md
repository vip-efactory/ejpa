# ejpa-spring-boot-starter 实现jar
这是一个基于SpringDataJPA的项目，从Controller-->Service-->Repository-->Entity,都有相应的模板代码,提供的功能有:
- CRUD
- 分页与排序 --JPA的默认实现
- 多字段复杂条件的查询
- 国际化--i18n
- 实现对实体属性的前端约束检查
- 支持多租户
- 支持Java8的日期时间类型
- 支持通过观察者模式来联动保证连表缓存的一致性;

# 具体使用案例
- https://github.com/vip-efactory/ejpa-example/blob/master/README.md

# TODO
- 对底层的数据库异常增加处理；
- 高级查询支持严格模式：严格模式下，条件错误则不再继续进行查询!!!
- 类似eladmin的连接查询的方式!
- 前端传递数据使用DTO接收，避免直接使用实体的一些隐藏的属性被利用!
- 封装常见OSS的上传下载，支持：本地/AliOSS等

# 注意
- 使用请直接使用中央仓库里的starter，例如：
```
 <ejpa.version>4.0.0</ejpa.version>
 <dependency>
    <groupId>vip.efactory</groupId>
    <artifactId>ejpa-spring-boot-starter</artifactId>
    <version>${ejpa.version}</version>
    <type>pom</type>
</dependency>
```


# 历史版本更新列表：
- 详见:https://docs.efactory.vip/ejpa/version.html


# 关于高级搜索，3.0支持以下场景的高级搜索：
- 单个条件：
    例如：A=3，A>=3 ,A is null等方式
   
- 多个条件：
    ```
    -- 所有默认或关系：A=4 || B=7 || C > 8
    -- 所有条件与关系：A=4 && B=7 && C > 8
    -- 与或混合关系：
        A=4 && B=7 || C > 8
    -- 半混合关系：
        A=4 && B =7 && (C =9 || C =11)
        A=4 || B =7 || (C =9 && D =11)
    -- 全混合关系：
        (A = 4 || B = 7) && (C =9 || C =11 )
        (A = 4 && B = 7) || (C =9 && D =11 )
    ```
