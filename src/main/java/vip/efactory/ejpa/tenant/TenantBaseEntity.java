package vip.efactory.ejpa.tenant;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import vip.efactory.ejpa.base.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;


/**
 * 对于同库同表的多租户模式，可以让需要多租户支持的表实体继承本实体
 */
@Getter
@Setter
@MappedSuperclass
@ApiModel(value = "基础租户实体", description = "所有需要支持租户数据表的通用部分")
public abstract class TenantBaseEntity<ID> extends BaseEntity<ID> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Description:租户id
     */
    @Column(name = "tenantId", length = 1024, columnDefinition = "varchar(1024) COMMENT '多租户id'")
    private String tenantId;

}
