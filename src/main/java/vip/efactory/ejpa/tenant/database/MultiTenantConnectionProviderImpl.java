package vip.efactory.ejpa.tenant.database;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import vip.efactory.ejpa.tenant.identifier.TenantConstants;

import javax.sql.DataSource;

/**
 * 这个类是Hibernate框架拦截sql语句并在执行sql语句之前更换数据源提供的类
 * @author dusuanyun
 */
public class MultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    /**
     * 在没有提供tenantId的情况下返回默认数据源
     */
    @Override
    protected DataSource selectAnyDataSource() {
        return TenantDataSourceProvider.getTenantDataSource(TenantConstants.DEFAULT_TENANT_ID.toString());
    }

    /**
     * 提供了tenantId的话就根据ID来返回数据源
     */
    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return TenantDataSourceProvider.getTenantDataSource(tenantIdentifier);
    }
}
