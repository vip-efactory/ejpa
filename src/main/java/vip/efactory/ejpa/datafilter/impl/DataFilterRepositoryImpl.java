//package vip.efactory.ejpa.datafilter.impl;
//
//import lombok.Setter;
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.util.CollectionUtils;
//import vip.efactory.ejpa.base.entity.BaseEntity;
//import vip.efactory.ejpa.datafilter.DataFilter;
//import vip.efactory.ejpa.datafilter.DataFilterCalculator;
//import vip.efactory.ejpa.datafilter.DataFilterRepository;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * Description:
// *
// * @Author dusuanyun
// * @Date 2021-04-10
// */
//public abstract class DataFilterRepositoryImpl<T extends BaseEntity, ID> implements DataFilterRepository<T, ID> {
//
//    @Setter
//    private DataFilterCalculator dataFilterCalculator;
//
//    @Override
//    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
//                            ResultHandler resultHandler, BoundSql boundSql) {
//        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
//
//        String originalSql = boundSql.getSql();
//        Object parameterObject = boundSql.getParameterObject();
//
//        // 查找参数中包含DataScope类型的参数
//        DataFilter dataFilter = findDataScopeObject(parameterObject);
//        if (dataFilter == null) {
//            return;
//        }
//        // 过滤的属性名
//        String scopeName = dataFilter.getFilterPropName();
//        List<Long> deptIds = dataFilter.getDeptIds();
//        // 优先获取赋值数据，不需要过滤
//        if (CollectionUtils.isEmpty(deptIds) && dataFilterCalculator.calcScope(deptIds)) {
//            originalSql = String.format("SELECT %s FROM (%s) temp_data_scope", dataFilter.getFunc().getType(), originalSql);
//            mpBs.sql(originalSql);
//            return;
//        }
//        // 全拦截，不返回
//        if (deptIds.isEmpty()) {
//            originalSql = String.format("SELECT %s FROM (%s) temp_data_scope WHERE 1 = 2", dataFilter.getFunc().getType(), originalSql);
//        } else {
//            // 过滤条件
//            String join = CollectionUtils.join(deptIds, ",");
//            originalSql = String.format("SELECT %s FROM (%s) temp_data_scope WHERE temp_data_scope.%s IN (%s)", dataFilter.getFunc().getType(), originalSql, scopeName, join);
//        }
//
//        mpBs.sql(originalSql);
//    }
//
//    /**
//     * 查找参数是否包括DataScope对象
//     * @param parameterObj 参数列表
//     * @return DataFilter
//     */
//    private DataFilter findDataScopeObject(Object parameterObj) {
//        if (parameterObj instanceof DataFilter) {
//            return (DataFilter) parameterObj;
//        }
//        else if (parameterObj instanceof Map) {
//            for (Object val : ((Map<?, ?>) parameterObj).values()) {
//                if (val instanceof DataFilter) {
//                    return (DataFilter) val;
//                }
//            }
//        }
//        return null;
//    }
//
//
//
//    @Override
//    public <S extends T> Iterable<S> getListByFilter(Specification<S> spec, DataFilter filter) {
//        return null;
//    }
//
//    @Override
//    public <S extends T> Page<S> getPageByFilter(Pageable pageable, Specification<S> spec, DataFilter filter) {
//        return null;
//    }
//
//    @Override
//    public <S extends T> long getCountByFilter(Specification<S> spec, DataFilter filter) {
//        return 0;
//    }
//
//    @Override
//    public <S extends T> Iterable<S> findAllByFilter(Example<S> example, DataFilter filter) {
//        return null;
//    }
//
//    @Override
//    public <S extends T> Page<S> findPageByFilter(Example<S> example, Pageable pageable, DataFilter filter) {
//        return null;
//    }
//
//    @Override
//    public <S extends T> long findCountByFilter(Example<S> example, DataFilter filter) {
//        return 0;
//    }
//}
