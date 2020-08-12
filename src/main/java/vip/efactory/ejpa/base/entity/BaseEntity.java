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
import java.time.LocalDateTime;


/**
 * "@EntityListeners(AuditingEntityListener.class)" 是用于监听实体类添加或者删除操作的
 * ID 为主键的泛型，子类继承需要指定ID的类型，这样将ID实现放在子类中，具有更广的适用性
 * 关于LocalDateTime等，参见：
 * https://mp.weixin.qq.com/s?__biz=MzI4Njk5OTg1MA==&mid=2247484398&idx=1&sn=40329619dc17ea8af6e6f16a41d1313d&chksm=ebd517abdca29ebda07d005eb1236b44d8fb8d230dc67b1daf79cddcecdd8b330ed39d4efb2b&scene=0&xtrack=1&key=65359c7fdab27a23bb00270ec7068f850ea8be0db518668ebd3f4750793b534a3a6837ecf817c67590d3ace8aa6f7c0adf010eac1f0477e634b7a6ecb94a160f10e9177fe53dec7b2a22a99b1efc2da1&ascene=1&uin=Mjg5NDEwNjA0MA%3D%3D&devicetype=Windows+XP&version=62060841&lang=zh_CN&exportkey=AT1%2BbVgO%2Fh%2FbAOVJ7ZJKtdI%3D&pass_ticket=rw2bMthKO3Odv2q5k4reG4dEDsvXwajBufPczFjV4o2FTl%2BbPI%2B7tJ%2F26NQGvXnm
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@ApiModel(value = "基础实体", description = "所有数据表的通用部分")
public abstract class BaseEntity<ID> extends BaseSearchEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    //    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @ApiModelProperty(name = "id", example = "1")
//    private Long id;
    // 将主键的set与get的实现移植到子类这样更具有灵活性
    public abstract ID getId();

    public abstract void setId(ID id);

    /**
     * Description:创建日期,数据库底层实现时间的创建
     */
    @Column(updatable = false, columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate //使用注解实现时间的创建
    @ApiModelProperty(hidden = true)
    private LocalDateTime createTime;

    /**
     * Description:最后更新日期,数据底层实现时间的更新
     */
    @Column(columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL COMMENT '更新时间'")
    //使用注解实现时间的更新
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate //使用注解实现更新时间的更新
    @ApiModelProperty(hidden = true)
    private LocalDateTime updateTime;


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
