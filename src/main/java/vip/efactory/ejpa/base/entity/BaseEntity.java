package vip.efactory.ejpa.base.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * "@EntityListeners(AuditingEntityListener.class)" 是用于监听实体类添加或者删除操作的
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@ApiModel(value = "基础实体", description = "所有数据表的通用部分")
public class BaseEntity extends BaseSearchEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(name = "id", example = "1")
    private Long id;

    /**
     * Description:创建日期,数据库底层实现时间的创建
     */
    @Column(updatable = false, columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate //使用注解实现时间的创建
    @ApiModelProperty(hidden = true)
    private Date createTime;

    /**
     * Description:最后更新日期,数据底层实现时间的更新
     */
    @Column(columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL COMMENT '更新时间'")
    //使用注解实现时间的更新
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate //使用注解实现更新时间的更新
    @ApiModelProperty(hidden = true)
    private Date updateTime;


    /**
     * Description:创建人编号
     */
    @Column(length = 32, columnDefinition = "varchar(32) COMMENT '创建人'")
    @CreatedBy
    @ApiModelProperty(hidden = true)
    private String creatorNum;

    /**
     * Description:更新人编号或者姓名,//不使用id，如果人员被删除，看到一个数字是无意义的。
     * 修改人
     */
    @Column(length = 32, columnDefinition = "varchar(32) COMMENT '更新人'")
    @LastModifiedBy
    @ApiModelProperty(hidden = true)
    private String updaterNum;

//    /**
//     * Description:实体的业务唯一编码，因为业务上的重复通常都不是用id
//     *
//     */
//    @Column(length = 32, columnDefinition = "varchar(32) COMMENT '实体业务编码'")
//    private String entityNum;

//    /**
//     * Description:实体名称
//     *
//     */
//    @Column(name = "name", length = 128, columnDefinition = "varchar(128) COMMENT '名称'")
//    private String name;

//    /**
//     * Description:实体名称拼音搜索码,可以将相关需要搜索的字段的搜索码都追加进来
//     *
//     */
//    @Column(length = 256, columnDefinition = "varchar(256) COMMENT '名称拼音搜索吗'")
//    @ApiModelProperty(hidden = true)
//    private String searchCode;

    /**
     * Description:备注
     */
    @Column(name = "remark", length = 1024, columnDefinition = "varchar(1024) COMMENT '备注'")
    private String remark;

}
