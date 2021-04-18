package vip.efactory.ejpa.datafilter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vip.efactory.common.base.utils.SpringContextHolder;

import java.util.HashSet;
import java.util.Set;

/**
 * 数据过滤条件对象，即，此对象决定返回全部数据还是部分数据
 *
 * @author dbdu
 */
@Setter
@Slf4j
public class DataFilter {

    /**
     * 过滤范围的属性名称,例如：deptId，userId
     */
    @Getter
    private String filterPropName = "deptId";

    /**
     * 具体的数据范围
     */
    private Set<Long> deptIds = new HashSet<>();

    /**
     * 默认只是本级部门
     */
    @Getter
    private DataFilterTypeEnum level = DataFilterTypeEnum.ONLY_THIS_LEVEL;

    public Set<Long> getDeptIds() {
        // 判断是否需要计算范围
        calculateScope();
        return deptIds;
    }

    /**
     * 判断是否需要计算来进一步确定数据权限范围
     */
    private void calculateScope() {
        // 如果level是本级与仅仅自己则不处理计算，即仅当时本级包含子级或者是仅仅是子级时才计算。
        if (DataFilterTypeEnum.ONLY_SELF.getType() == level.getType() || DataFilterTypeEnum.ONLY_THIS_LEVEL.getType() == level.getType()) {
            return;
        }

        // 从Spring容器中获取对应的bean，有则使用，无责不处理
        try {
            DataFilterCalculator calculator = SpringContextHolder.getBean(DataFilterCalculator.class);
            calculator.calculateScope(this);
        } catch (Exception e) {
            // 如果取不到打印警告日志
            log.warn(e.getMessage());
        }
    }

}
