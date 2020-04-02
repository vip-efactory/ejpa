package vip.efactory.ejpa.config.tenant.ds;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import vip.efactory.ejpa.config.tenant.id.TenantConstants;
import vip.efactory.ejpa.config.tenant.id.TenantHolder;

/**
 * 这个类是由Hibernate提供的用于识别tenantId的类，当每次执行sql语句被拦截就会调用这个类中的方法来获取tenantId
 */
public class MultiTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    // 获取tenantId的逻辑在这个方法里面写
    @Override
    public String resolveCurrentTenantIdentifier() {
        if (!"".equals(TenantHolder.getTenantId().toString())) {
            return TenantHolder.getTenantId().toString();
        }
        return TenantConstants.DEFAULT_TENANT_ID.toString();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
