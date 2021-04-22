package vip.efactory.ejpa.datafilter;

/**
 * data scope 判断处理器,抽象服务扩展，应用需要有实现此接口bean，以便处理包含子级及递归的情况
 *
 * @author dbdu
 */
public interface DataFilterCalculator {

    /**
     * 判断是否需要计算来进一步确定数据权限范围
     *
     * @param dataFilter 数据过滤类型
     */
    void calculateScope(final DataFilter dataFilter);

}
