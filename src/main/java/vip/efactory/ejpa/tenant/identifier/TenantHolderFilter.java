package vip.efactory.ejpa.tenant.identifier;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * 利用过滤器的方式,拦截获取请求中的租户信息，
 * 注意此过滤器的执行优先级要求极高，因为租户信息是非常重要的关键信息，必须最先拿到！！
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantHolderFilter extends GenericFilterBean {

    /**
     * 从请求头中获取指定的租户信息
     */
    @Override
    @SneakyThrows
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 获取请求头中的TENANT_ID的值
        String tenantId = request.getHeader(TenantConstants.TENANT_ID);

        if (!StringUtils.isEmpty(tenantId)) {
            TenantHolder.setTenantId(Long.parseLong(tenantId));
        } else {
            TenantHolder.setTenantId(TenantConstants.DEFAULT_TENANT_ID);
            log.info("当前请求中没有租户信息，使用默认的租户ID为:{}", TenantConstants.DEFAULT_TENANT_ID);
        }

        filterChain.doFilter(request, response);
        // 请求结束，移除租户信息
        TenantHolder.remove();
    }
}
