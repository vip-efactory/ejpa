package vip.efactory.ejpa.datafilter;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限类型枚举，这里不需要提供【全部】的范围，如果真是需要全部，不使用数据过滤就是全部！
 * @author dbdu
 */
@Getter
@AllArgsConstructor
public enum DataFilterTypeEnum {

	/**
	 * 自定义部门，例如：单个实体记录指定哪些部门可见，哪些用户可看见，暂时不支持既是部分部门又是部分用户的场景
	 * 这种可以在实体的加一个字段专门用于保存自定义的部门id或者用户Id
	 */
	CUSTOM(1, "自定义部门或者用户"),

	/**
	 * 本级及子级部门
	 */
	OWN_AND_CHILD_LEVEL(2, "本级及子级部门"),

	/**
	 * 本级
	 */
	ONLY_THIS_LEVEL(3, "本级"),

	/**
	 * 仅子级部门数据，不包含本机数据
	 */
	ONLY_CHILD(4, "仅子级数据"),

	/**
	 * 仅仅是自己
	 */
	ONLY_SELF(5, "仅仅是自己"),
	;

	/**
	 * 类型
	 */
	private final int type;

	/**
	 * 描述
	 */
	private final String description;

}
