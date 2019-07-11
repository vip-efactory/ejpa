# ejpa-spring-boot-starter 实现jar
这是一个基于SpringDataJPA的项目，从Controller-->Service-->Repository-->Entity,都有相应的模板代码,提供的功能有:
- CRUD
- 分页与排序 --JPA的默认实现
- 多字段复杂条件的查询

# 关于主键
- 目前是写死的主键类型为Long并且为自增长,暂时不支持其他类型；

# 计划
- 从BaseEntity中移除主键定义,以便转移到子类中定义,这样用户可配置性就比较大;
- 在模板中因为没有id的key属性,在BaseController中,可以考虑使用反射来获取;

# 权衡
- 在考虑是否使用Spring DataJPA自己的响应实体ResponseEntity,个人认为没有现在的R简洁简单

# 注意
- 请不要直接使用master分支的代码,在开发中可能非常不稳定,可以使用发布的分支!
