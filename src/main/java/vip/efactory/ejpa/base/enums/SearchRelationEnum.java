package vip.efactory.ejpa.base.enums;

import lombok.Getter;

/**
 * Description: 多个实体属性间的查询条件关系,
 * 例如:有两个查询属性条件,name和age,是两个条件都满足还是只需要满足任意一个即可.
 *
 * @author dbdu
 * Created at:2019-03-09 16:06,
 */
@Getter
public enum SearchRelationEnum {
    //NOT(-1, "非关系--条件取反"),
    OR(0, "或关系--满足任一条件"),
    AND(1, "与关系--所有条件满足");

    // 枚举值
    private int value;

    //枚举说明
    private String desc;

    SearchRelationEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static SearchRelationEnum getByValue(int value) {
        switch (value) {
//            case -1:
//                return NOT;
            case 0:
                return OR;
            case 1:
                return AND;
            default:
                return OR;
        }

    }


}
