package vip.efactory.ejpa.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Description:JPA中的PO持久化，会用null值更新已有的属性值，有时候这并不是我们想要的，这个工具类就是做这个事情
 * 同时支持要复制又不全部复制的做法！
 *
 * @param
 * @author dbdu
 * @date 18-4-19 下午4:48
 * @return
 */
public class UpdatePoUtil {
    /**
     * 将目标源中不为空的字段过滤，将数据库中查出的数据源复制到提交的目标源中
     *
     * @param source           用id从数据库中查出来的数据源
     * @param target           提交的实体，目标源
     * @param ignoreProperties 忽略的属性，比如有些属性更新时间需要每次都变，可以不复制
     */
    public static void copyNullProperties(Object source, Object target, String... ignoreProperties) {
        String[] notCopyProperties = getNoNullProperties(target, ignoreProperties);
        BeanUtils.copyProperties(source, target, notCopyProperties);
    }

    /**
     * @param target 目标源数据
     * @return 将目标源中不为空的字段取出, 并将传进来的不需要复制的字段合并
     */
    private static String[] getNoNullProperties(Object target, String... ignoreProperties) {
        BeanWrapper srcBean = new BeanWrapperImpl(target);
        PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        Set<String> noEmptyName = new HashSet<>();
        for (PropertyDescriptor p : pds) {
            Object value = srcBean.getPropertyValue(p.getName());
            if (value != null) noEmptyName.add(p.getName());
        }
        //将传进来的主动要忽略的字段进行合并
        noEmptyName.addAll(Arrays.asList(ignoreProperties));

        String[] result = new String[noEmptyName.size()];
        return noEmptyName.toArray(result);
    }
}
