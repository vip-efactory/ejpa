package vip.efactory.ejpa.base.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 系统里的租户管理表
 * @author dusuanyun
 */
@Getter
@Setter
@MappedSuperclass
//@Entity
//@Table(name = "sys_tenant")
public abstract class TenantEntity<ID> extends BaseEntity<ID> {

    // 租户的ID，例如：0L,主键的定义留给子类，这样子类也可以进行字段扩展

    /**
     * 租户的名称,例如：电子工厂有限公司
     */
    @Column(name = "tenant_name")
    private String tenantName;

    /**
     * 租户的编码：例如：ZKF08KL
     */
    @Column(name = "tenant_Code")
    private String tenantCode;

    // 以下3个字段如果是基于不同数据库实例的多租户，则需要配置
    /**
     * 当前租户需要连接数据库的JDBC链接
     */
    @Column(name = "jdbcUrl")
    private String jdbcUrl;

    /**
     * 当前租户需要连接数据库的驱动类
     */
    @Column(name = "driverClassName")
    private String driverClassName;
    /**
     * 当前租户需要连接数据库的用户名
     */
    @Column(name = "dbUsername")
    private String dbUsername;

    /**
     * 当前租户需要连接数据库的密码
     */
    @Column(name = "dbPassword")
    private String dbPassword;

}
