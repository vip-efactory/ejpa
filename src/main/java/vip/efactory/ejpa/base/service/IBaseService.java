package vip.efactory.ejpa.base.service;


import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vip.efactory.ejpa.base.entity.BaseEntity;

import java.util.List;
import java.util.Optional;

/**
 * Description:服务层接口的父接口，继承此接口默认下面的这些方法要实现的，采用泛型的写法
 *
 * @author dbdu
 */
public interface IBaseService<T extends BaseEntity, ID> {

    /**
     * Description:查询部分的方法
     *
     * @author dbdu
     */
    //List<T> findAll();

    Iterable<T> findAll();

    Page<T> findAll(Pageable var1);

    //List<T> findAll(Sort var1);

    Iterable<T> findAll(Sort var1);

    //List<T> findAllById(Iterable<ID> var1);

    Iterable<T> findAllById(Iterable<ID> var1);

    T getOne(ID var1);

    Optional<T> findById(ID var1);

    <S extends T> List<S> findAll(Example<S> var1);

    <S extends T> List<S> findAll(Example<S> var1, Sort var2);


    /**
     * Description:下面是保存的方法
     *
     * @author dbdu
     */
    //<S extends T> List<S> saveAll(Iterable<S> var1);

    <S extends T> Iterable<S> saveAll(Iterable<S> var1);

    void flush();

    <S extends T> S saveAndFlush(S var1);


    <S extends T> S save(S var1);


    /**
     * Description:下面是删除的方法
     *
     * @author dbdu
     */
    void delete(T var1);

    void deleteById(ID var1);

    void deleteAll();

    void deleteAll(Iterable<? extends T> var1);

    void deleteAllInBatch();

    void deleteInBatch(Iterable<T> var1);

    /**
     * Description:下面是检查存在性及计数
     *
     * @param var1 id主键
     * @return
     * @author dbdu
     */
    boolean existsById(ID var1);

    long count();


    /**
     * Description:上面的方法是框架自带的，下面的是用户自定义的方法
     * @author dbdu
     */

    /**
     * Description:使用主键批量删除
     *
     * @param var1 可迭代的id集合
     * @return void
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

    /**
     * Description:根据实体的编号，判断数据库中是否存在实体
     *
     * @param entityNum 实体编码
     * @return java.lang.Boolean
     * @author dbdu
     */
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
     * @date 19-7-5 下午12:15
     */
    Page<T> advancedQuery(T entity, Pageable pageable);


}
