package vip.efactory.ejpa.config.tenant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vip.efactory.ejpa.config.tenant.id.MultiTenantInterceptor;

/**
 * 多租户配置类
 */
@Configuration
public class TenantConfig implements WebMvcConfigurer {


    /**
     * 多租户解析解析拦截器
     */
    @Bean
    public MultiTenantInterceptor multiTenantInterceptor() {
        MultiTenantInterceptor multiTenantInterceptor = new MultiTenantInterceptor();
        return multiTenantInterceptor;
    }

    /**
     * 注册租户信息拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(multiTenantInterceptor());
    }

}
