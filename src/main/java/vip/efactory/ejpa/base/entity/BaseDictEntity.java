package vip.efactory.ejpa.base.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;


/**
 * 字典的基础表的实体
 */
@Getter
@Setter
@MappedSuperclass
public class BaseDictEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 字典名称
     */
    @NotBlank(message = "字典名称不能为空")
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    /**
     * 字典值
     */
    @NotBlank(message = "字典内容不能为空")
    @Column(name = "value", length = 1000, columnDefinition = "varchar(1000) COMMENT '字典值'")
    private String value;

    /**
     * 字典码
     */
    @NotBlank(message = "字段编码不能为空")
    @Column(name = "code", length = 100, columnDefinition = "varchar(100) COMMENT '字典码'")
    private String code;

    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
    @Column(name = "type", length = 100, columnDefinition = "varchar(100) COMMENT '字典类型'")
    private String type;

    @Column(name = "order_num", columnDefinition = "int COMMENT '排序'")
    private int orderNum;

    /**
     * 删除标记 -1：已删除，0：正常
     */
    private Integer delFlag;
}
