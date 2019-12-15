package vip.efactory.ejpa.base.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description: 需要搜索的字段定义,这里的字段是指实体的属性,
 * 例1:实体属性名称为zhangsan的精确条件,则name值为name,searchType值为1,val值为zhangsan
 * 例2:实体属性年龄在25-35之间的范围条件,则name值为age,searchType值为2,val值为25,val2值为35
 *
 * @author dbdu
 * Created at:2019-03-09 14:37,
 */
@Data
@ApiModel(value = "条件实体", description = "仅在需要高级搜索的接口中,这个才需要")
public class BaseSearchField {

    /**
     * 字段名，例如，name,password等
     */
    @ApiModelProperty(value = "条件字段名", name = "name", notes = "例如:是name字段或者age字段")
    private String name;

    /**
     * 搜索类型：
     * ０－－模糊查询；
     * １－－精准查询，严格一样
     * ２－－范围查询
     * 更多类型,参见SearchTypeEnum
     */
    @ApiModelProperty(value = "搜索类型", name = "searchType", notes = "例如:0--模糊查询;1--等于查询;2--范围查询;3--不等于查询;4--小于查询;5--小于等于查询;6--大于查询;7--大于等于查询")
    private Integer searchType;

    /**
     * 搜索字段值,暂时用String来接收所有类型的值！
     * 搜索类型为０或１默认取此值，为２范围查询时，此值是开始值
     */
    @ApiModelProperty(value = "字段值或开始值", name = "val", notes = "搜索类型为０或１默认取此值，为２范围查询时，此值是开始值")
    private String val;

    /**
     * 搜索类型为０或１时此值不用，为２范围查询时，此值是结束值
     */
    @ApiModelProperty(value = "结束值", name = "val2", notes = "搜索类型为０或１时此值不用，为２范围查询时，此值是结束值")
    private String val2;

    /**
     * 条件位置顺序，例如都是同级，一级的情况下，这个条件是在前面还是后面。可不写，有则使用，没有则随机排
     */
    @ApiModelProperty(value = "条件位置顺序", name = "order", notes = "条件级别，1级，2级...  1级条件就是最外层条件，")
    private Integer order;

    /**
     * 逻辑类型,当前条件是与的关系还是或的关系，0 为或的关系，1为与的关系   若为空，默认为0
     */
    @ApiModelProperty(value = "条件逻辑类型", name = "logicalType", notes = "当前条件是与的关系还是或的关系，0 为或的关系，1为与的关系；若为空，默认为0")
    private Integer logicalType;

    /**
     * 括号组，哪些条件在同一个组里，例如：（a=3 || b=4） && （c=5 || d=7），简单条件时允许为空
     */
    @ApiModelProperty(value = "括号组", name = "bracketsGroup", notes = "哪些条件在同一个组里，例如：（a=3 || b=4） && （c=5 || d=7），简单条件时允许为空")
    private String bracketsGroup;

    /**
     * 组逻辑类型,当前组条件是与的关系还是或的关系，0 为或的关系，1为与的关系   若为空，默认为0
     *  是||（a=3 || b=4）
     *  还是 &&（c=5 || d=7）
     */
    @ApiModelProperty(value = "组逻辑类型", name = "logicalTypeGroup", notes = "当前组条件是与的关系还是或的关系，0 为或的关系，1为与的关系；若为空，默认为0")
    private Integer logicalTypeGroup;

//
//    /**
//     * name名称对应的字段的数据类型，例如：String,Integer,Date,Number等,JPA实现between查询必须要有
//     */
//    private String type;

}
