package vip.efactory.ejpa.datafilter;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限类型枚举
 * @author dbdu
 */
@Getter
@AllArgsConstructor
public enum DataFilterTypeEnum {

	/**
	 * 查询全部数据
	 */
	ALL(0, "全部"),

	/**
	 * 自定义部门，这种可以在实体的加一个字段专门用于保存自定义的部门id或者用户Id
	 */
	CUSTOM(1, "自定义部门"),

	/**
	 * 本级及子级部门
	 */
	OWN_CHILD_LEVEL(2, "本级及子级部门"),

	/**
	 * 本级
	 */
	OWN_LEVEL(3, "本级"),

	/**
	 * 仅仅是自己
	 */
	ONLY_SELF(4, "仅仅是自己"),
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
