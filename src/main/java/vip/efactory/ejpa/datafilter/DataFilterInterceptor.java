package vip.efactory.ejpa.datafilter;

import cn.hutool.core.collection.CollectionUtil;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.util.CollectionUtils;
import vip.efactory.common.base.utils.SpringContextHolder;

import java.util.List;

/**
 * 在即将执行之前变更执行语句，以便实现数据过滤
 *
 * @author dusuanyun
 */
public class DataFilterInterceptor implements StatementInspector {

    @Override
    public String inspect(String sql) {
        System.out.println("进入拦截器" + sql);
        String originalSql = sql;

        // 查找参数中包含DataScope类型的参数
        DataFilter dataFilter = DataFilterContextHolder.getDataFilter();
        if (dataFilter == null) {
            // 没有数据过滤对象,使用默认的sql执行
            return originalSql;
        }

        String scopeName = dataFilter.getFilterPropName();
        List<Long> deptIds = dataFilter.getDeptIds();
        // 优先获取赋值数据
        DataFilterCalculator dataFilterCalculator = SpringContextHolder.getBean(DataFilterCalculator.class);
        if (CollectionUtils.isEmpty(deptIds) && dataFilterCalculator.calcScope(deptIds)) {
            originalSql = String.format("SELECT %s FROM (%s) tmp_tbl_data_filter", dataFilter.getFunc().getType(),
                    originalSql);
            return originalSql;
        }

        if (deptIds.isEmpty()) {
            originalSql = String.format("SELECT %s FROM (%s) tmp_tbl_data_filter WHERE 1 = 2", dataFilter.getFunc().getType(), originalSql);
        } else {
            String join = CollectionUtil.join(deptIds, ",");
            originalSql = String.format("SELECT %s FROM (%s) tmp_tbl_data_filter WHERE tmp_tbl_data_filter.%s IN (%s)",
                    dataFilter.getFunc().getType(), originalSql, scopeName, join);
        }
        return sql;
    }
}
