//package vip.efactory.ejpa.datafilter;
//
//
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.querydsl.QuerydslPredicateExecutor;
//import org.springframework.data.repository.NoRepositoryBean;
//import vip.efactory.ejpa.base.entity.BaseEntity;
//
///**
// * 数据过滤持久层，支持数据权限 和批量插入
// *
// * @author dbdu
// */
//@NoRepositoryBean
//public interface DataFilterRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID>, QuerydslPredicateExecutor<T> {
//
//    /**
//     * 根据查询条件及过滤条件查询列表数据
//     *
//     * @param spec   查询条件
//     * @param filter 数据过滤条件
//     * @return List<T>
//     */
//    <S extends T> Iterable<S> getListByFilter(Specification<S> spec, DataFilter filter);
//
//    /**
//     * 根据查询条件及过滤条件查询分页数据
//     *
//     * @param pageable 分页参数对象
//     * @param spec     查询条件
//     * @param filter   数据过滤条件
//     * @return Page<T>
//     */
//    <S extends T> Page<S> getPageByFilter(Pageable pageable, Specification<S> spec, DataFilter filter);
//
//    /**
//     * 根据查询条件及过滤条件查询总共有的记录数量
//     *
//     * @param spec   查询条件
//     * @param filter 数据过滤条件
//     * @return long 数值
//     */
//    <S extends T> long getCountByFilter(Specification<S> spec, DataFilter filter);
//
//
//    /**
//     * 使用基于example的查询条件及过滤条件查询所有的数据
//     *
//     * @param example 查询条件
//     * @param filter  数据过滤条件
//     * @param <S>     实体或者实体的子类
//     * @return 集合
//     */
//    <S extends T> Iterable<S> findAllByFilter(Example<S> example, DataFilter filter);
//
//    /**
//     * 使用基于example的查询条件及过滤条件查询分页数据
//     *
//     * @param example  查询条件
//     * @param pageable 分页条件
//     * @param filter   数据过滤条件
//     * @param <S>      实体或者实体的子类
//     * @return 分页数据
//     */
//    <S extends T> Page<S> findPageByFilter(Example<S> example, Pageable pageable, DataFilter filter);
//
//    /**
//     * 使用基于example的查询条件及过滤条件查询匹配的记录数量
//     *
//     * @param example 查询条件
//     * @param filter  数据过滤条件
//     * @param <S>     实体或者实体的子类
//     * @return 匹配的数量
//     */
//    <S extends T> long findCountByFilter(Example<S> example, DataFilter filter);
//
//}
