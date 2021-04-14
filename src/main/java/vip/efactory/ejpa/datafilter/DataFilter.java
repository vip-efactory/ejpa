package vip.efactory.ejpa.datafilter;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据过滤条件对象，即，此对象决定返回全部数据还是部分数据
 * @author dbdu
 */
@Data
//@EqualsAndHashCode(callSuper = true)
//public class DataFilter extends HashMap  {
public class DataFilter {

	/**
	 * 过滤范围的属性名称,例如：deptId，userId
	 */
	private String filterPropName = "deptId";

	/**
	 * 具体的数据范围
	 */
	private List<Long> deptIds = new ArrayList<>();

	/**
	 * 默认只是本级部门
	 */
	private DataFilterTypeEnum level = DataFilterTypeEnum.OWN_LEVEL;

}
