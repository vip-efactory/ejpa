package vip.efactory.ejpa.tenant.database;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import vip.efactory.ejpa.tenant.identifier.TenantConstants;
import vip.efactory.ejpa.tenant.identifier.TenantHolder;

/**
 * 这个类是由Hibernate提供的用于识别tenantId的类，当每次执行sql语句被拦截就会调用这个类中的方法来获取tenantId
 * @author dusuanyun
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
