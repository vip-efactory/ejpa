package vip.efactory.ejpa.base.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import java.util.Set;

/**
 * Description: 基础的高级搜索实体,不需要持久化到数据库
 *
 * @author dbdu
 * Created at:2019-03-09 10:57,
 */
@Setter
@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)      //null值的属性JSON不输出
@ApiModel(value = "高级搜索条件实体", description = "仅在需要高级搜索的接口中,这个才需要")
public class BaseSearchEntity {
    /**
     * Description: 关联类型：数据库不存这个字段
     * ０--或的关系，满足任意一个条件即可；
     * １--与的关系，满足所有条件；
     * －１--非的关系，条件取反；
     */
    @Transient
    @ApiModelProperty(hidden = true)
    // @ApiModelProperty(value = "多条件之间关系", name = "relationType", notes = "０--或的关系，满足任意一个条件即可;１--与的关系，满足所有条件；")
    private Integer relationType;

    /**
     * 数据库不存这个字段
     * 所有的搜索条件字段,不允许重复
     */
    @Transient
    @ApiModelProperty(hidden = true)
    //  @ApiModelProperty(value = "多条件集合", name = "conditions", notes = "不重复的条件集合")
    private Set<BaseSearchField> conditions;

}

