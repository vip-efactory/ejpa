package vip.efactory.ejpa.base.service;


import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vip.efactory.ejpa.base.entity.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Description:服务层接口的父接口，继承此接口默认下面的这些方法要实现的，采用泛型的写法
 *
 * @author dbdu
 */
public interface IBaseService<T extends BaseEntity, ID> {

    //查询部分的方法
    //List<T> findAll();

    /**
     * Description:
     *
     * @return Iterable
     */
    Iterable<T> findAll();

    /**
     * Description:
     *
     * @param var1 分页对象
     * @return Page
     */
    Page<T> findAll(Pageable var1);

    //List<T> findAll(Sort var1);

    /**
     * Description:
     *
     * @param var1 sort对象
     * @return Iterable
     */
    Iterable<T> findAll(Sort var1);

    //List<T> findAllById(Iterable<ID> var1);

    /**
     * Description:
     *
     * @param var1 可迭代的id集合
     * @return java.lang.Iterable
     */
    Iterable<T> findAllById(Iterable<ID> var1);

    /**
     * Description:
     *
     * @param var1 id
     * @return T
     */
    T getOne(ID var1);

    /**
     * Description:
     *
     * @param var1 id
     * @return java.util.Optional
     */
    Optional<T> findById(ID var1);

    /**
     * Returns a single entity matching the given {@link Example} or {@literal null} if none was found.
     *
     * @param example must not be {@literal null}.
     * @return a single entity matching the given {@link Example} or {@link Optional#empty()} if none was found.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if the Example yields more than one result.
     */
    <S extends T> Optional<S> findOne(Example<S> example);

    /**
     * Returns all entities matching the given {@link Example}. In case no match could be found an empty {@link Iterable}
     * is returned.
     *
     * @param example must not be {@literal null}.
     * @return all entities matching the given {@link Example}.
     */
    <S extends T> Iterable<S> findAll(Example<S> example);

    /**
     * Returns all entities matching the given {@link Example} applying the given {@link Sort}. In case no match could be
     * found an empty {@link Iterable} is returned.
     *
     * @param example must not be {@literal null}.
     * @param sort    the {@link Sort} specification to sort the results by, must not be {@literal null}.
     * @return all entities matching the given {@link Example}.
     * @since 1.10
     */
    <S extends T> Iterable<S> findAll(Example<S> example, Sort sort);

    /**
     * Returns a {@link Page} of entities matching the given {@link Example}. In case no match could be found, an empty
     * {@link Page} is returned.
     *
     * @param example  must not be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return a {@link Page} of entities matching the given {@link Example}.
     */
    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);


    // 下面是保存的方法

    //<S extends T> List<S> saveAll(Iterable<S> var1);

    /**
     * Description:
     *
     * @param var1 可迭代的实体集合
     * @return java.lang.Iterable
     */
    <S extends T> Iterable<S> saveAll(Iterable<S> var1);

    /**
     * Description:
     */
    void flush();

    /**
     * Description:
     *
     * @param var1 实体
     * @return S
     */
    <S extends T> S saveAndFlush(S var1);

    /**
     * Description:
     *
     * @param var1 实体
     * @return S
     */
    <S extends T> S save(S var1);

    // 下面是删除的方法

    /**
     * Description:
     *
     * @param var1 实体
     */
    void delete(T var1);

    /**
     * @param var1 id
     */
    void deleteById(ID var1);

    /**
     * Description:
     */
    void deleteAll();

    /**
     * Description:
     *
     * @param var1 可迭代的对象
     */
    void deleteAll(Iterable<? extends T> var1);

    /**
     * Description:
     */
    void deleteAllInBatch();

    /**
     * Description:
     *
     * @param var1 可迭代的对象
     */
    void deleteInBatch(Iterable<T> var1);

    // 下面是检查存在性及计数

    /**
     * Description:
     *
     * @param var1 id主键
     * @return boolean
     */
    boolean existsById(ID var1);

    /**
     * Description:
     *
     * @return long
     */
    long count();

    /**
     * Returns the number of instances matching the given {@link Example}.
     *
     * @param example the {@link Example} to count instances for. Must not be {@literal null}.
     * @return the number of instances matching the {@link Example}.
     */
    <S extends T> long count(Example<S> example);

    /**
     * Checks whether the data store contains elements that match the given {@link Example}.
     *
     * @param example the {@link Example} to use for the existence check. Must not be {@literal null}.
     * @return {@literal true} if the data store contains elements that match the given {@link Example}.
     */
    <S extends T> boolean exists(Example<S> example);

    // 上面的方法是框架自带的，下面的是用户自定义的方法


    /**
     * Description: 根据实体的属性名称判断，实体是否存在,
     * 注意：使用此方法，要自己保证属性名称的正确性，否则抛异常！
     * @param propertyName 实体的属性名，暂时支持字符串类型
     * @param propertyValue 实体的属性名对应的值,仅支持简单的基本类型的值为字符串的，不支持其他的自定义类的类型
     * @return boolean true实体存在；false 不存在。
     */
    boolean existsByEntityProperty(String propertyName,String propertyValue) throws NoSuchFieldException;

    /**
     * Description:使用主键批量删除
     *
     * @param var1 可迭代的id集合
     * @return int
     * @author dbdu
     */
    int deleteAllById(Iterable<ID> var1);

    /**
     * Description:更新实体的方法，很多时候保存和更新的处理逻辑是不一样的，权限也是不一样的，所以单独分开
     *
     * @param var1 要更新的实体
     * @return S
     * @author dbdu
     */
    <S extends T> S update(S var1);

//    /**
//     * Description:根据实体的编号，判断数据库中是否存在实体
//     *
//     * @param entityNum 实体编码
//     * @return java.lang.Boolean
//     * @author dbdu
//     */
//    Boolean existsByEntityNum(String entityNum);

    /**
     * Description: 高级模糊查询,查询条件在实体中
     *
     * @param entity 包含高级查询条件的实体
     * @return java.util.List&lt;T&gt;
     * @author dbdu
     */
    List<T> advancedQuery(T entity);

    /**
     * Description: 高级模糊查询及分页
     *
     * @param entity   包含高级查询条件的实体
     * @param pageable 分页参数对象
     * @return org.springframework.data.domain.Page&lt;T&gt;
     * @author dbdu
     */
    Page<T> advancedQuery(T entity, Pageable pageable);


    /**
     * 查询某个属性集合,不包含重复数据
     *
     * @param property 驼峰式的属性
     * @param value    模糊查询的value值
     * @return Set 集合
     */
    Set advanceSearchProperty(String property, String value);


}
