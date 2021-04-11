package vip.efactory.ejpa.datafilter;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.experimental.UtilityClass;

/**
 * 当前数据范围的过滤条件持有者，
 * @author dusuanyun
 */
@UtilityClass
public class DataFilterContextHolder {
    /**
     * 使用阿里的实现保存数据过滤对象
     */
    private final ThreadLocal<DataFilter> LOCAL_DATA_FILTER = new TransmittableThreadLocal<>();

    /**
     * 保存数据过滤对象
     */
    public void setDataFilter(DataFilter dataFilter) {
        LOCAL_DATA_FILTER.set(dataFilter);
    }

    /**
     * 获取数据过滤对象
     */
    public DataFilter getDataFilter() {
        return LOCAL_DATA_FILTER.get();
    }

    /**
     * 删除数据过滤对象
     */
    public void removeDataFilter() {
        LOCAL_DATA_FILTER.remove();
    }
}
