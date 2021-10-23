package vip.efactory.ejpa.tenant.column;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import vip.efactory.ejpa.base.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;


/**
 * 包说明：
 * column包里是基于列多租户模式用到的文件
 * database包里是基于数据库及schema多租户模式用到的文件
 * identifier包是多租户id信息的处理
 * 对于同库同表的多租户模式，可以让需要多租户支持的表实体继承本实体
 * @author dusuanyun
 */
@Getter
@Setter
@MappedSuperclass
@ApiModel(value = "基础租户实体", description = "所有需要支持租户数据表的通用部分")
@FilterDef(name = "tenantJpaFilter", parameters = {@ParamDef(name = "tenantId", type = "long")})
@Filter(name = "tenantJpaFilter", condition = "tenant_id = :tenantId")
public abstract class TenantBaseEntity<ID> extends BaseEntity<ID> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Description:租户id
     */
    @Column(name = "tenantId", columnDefinition = "bigint(20) COMMENT '多租户id'")
    private Long tenantId;

}
