package vip.efactory.ejpa.tenant.database;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import vip.efactory.ejpa.base.entity.TenantEntity;
import vip.efactory.ejpa.tenant.identifier.TenantConstants;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 租户数据源提供者
 */
@Slf4j
@AllArgsConstructor
public class TenantDataSourceProvider {
    // 使用一个map来存储我们租户和对应的数据源，租户和数据源的信息就是从我们的tenant表中读出来
    private static Map<String, DataSource> dataSourceMap = new HashMap<>();

    // 根据传进来的tenantId决定返回的数据源
    public static DataSource getTenantDataSource(String tenantId) {
        if (dataSourceMap.containsKey(tenantId)) {
            log.info("Get Tenant {} DataSource", tenantId);
            return dataSourceMap.get(tenantId);
        } else {
            log.info("Get Default Tenant DataSource.");
            return dataSourceMap.get(TenantConstants.DEFAULT_TENANT_ID.toString());
        }
    }

    // 初始化的时候用于添加数据源的方法
    public static void addDataSource(String tenantId, DataSource dataSource) {
        dataSourceMap.put(tenantId, dataSource);
    }

    // 初始化的时候用于添加数据源的方法
    public static void addDataSource(TenantEntity tenantEntity) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(tenantEntity.getJdbcUrl());
        dataSourceBuilder.username(tenantEntity.getDbUsername());
        dataSourceBuilder.password(tenantEntity.getDbPassword());
        dataSourceBuilder.driverClassName(tenantEntity.getDriverClassName());
        dataSourceMap.put(tenantEntity.getId().toString(), dataSourceBuilder.build());
    }

    // 根据传进来的tenantId来删除指定的数据源
    public static void removeDataSource(String tenantId) {
        if (dataSourceMap.containsKey(tenantId)) {
            log.info("Remove Tenant {} DataSource", tenantId);
            dataSourceMap.remove(tenantId);
        }
    }

    // 刷新指定租户的数据源
    public static void refreshDataSource(TenantEntity tenantEntity) {
        log.info("Refresh Tenant {} DataSource", tenantEntity.getId());
        addDataSource(tenantEntity);
    }
}
