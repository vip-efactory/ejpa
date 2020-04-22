package vip.efactory.ejpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vip.efactory.ejpa.tenant.identifier.TenantHolderFilter;

/**
 * 多租户配置类
 */
@Configuration
//public class TenantConfig implements WebMvcConfigurer {
public class TenantConfig {

    @Bean
    public TenantHolderFilter tenantHolderFilter() {
        return new TenantHolderFilter();
    }

//    /**
//     * 多租户解析解析拦截器
//     */
//    @Bean
//    public MultiTenantInterceptor multiTenantInterceptor() {
//        MultiTenantInterceptor multiTenantInterceptor = new MultiTenantInterceptor();
//        return multiTenantInterceptor;
//    }
//
//    /**
//     * 注册租户信息拦截器
//     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(multiTenantInterceptor());
//    }

}
