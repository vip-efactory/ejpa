package vip.efactory.ejpa.config.tenant.id;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.experimental.UtilityClass;

/**
 * 当前请求的租户持有者，
 */
@UtilityClass
public class TenantHolder {
    // 使用阿里的实现
    private final ThreadLocal<Long> LOCAL_TENANT = new TransmittableThreadLocal<>();

    /**
     * 保存租户的ID
     */
    public void setTenantId(Long tenantId) {
        LOCAL_TENANT.set(tenantId);
    }

    /**
     * 获取租户信息
     */
    public Long getTenantId() {
        return LOCAL_TENANT.get() == null ? TenantConstants.DEFAULT_TENANT_ID : LOCAL_TENANT.get();
//        return LOCAL_TENANT.get();
    }

    /**
     * 删除租户信息，通常用不到
     */
    public void remove() {
        LOCAL_TENANT.remove();
    }
}
