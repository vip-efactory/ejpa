package vip.efactory.ejpa.tenant.identifier;

/**
 * 租户的相关常量定义
 * @author dusuanyun
 */
public interface TenantConstants {
    /**
     * 请求头中的租户ID字段名称
     */
    String TENANT_ID = "TENANT_ID";

    /**
     * 默认的租户ID,默认为０
     */
    Long DEFAULT_TENANT_ID = 0L;
}
