package vip.efactory.ejpa.config.tenant.id;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 租户信息拦截器，如果获取不到就使用默认的租户，目前有两种方式实现，本例还有过滤器的实现！
 */
@Slf4j
public class MultiTenantInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader(TenantConstants.TENANT_ID);
        if (!StringUtils.isEmpty(tenantId)) {
            TenantHolder.setTenantId(Long.parseLong(tenantId));
        } else {
            TenantHolder.setTenantId(TenantConstants.DEFAULT_TENANT_ID);
            log.info("当前请求中没有租户信息，使用默认的租户ID为:{}", TenantConstants.DEFAULT_TENANT_ID);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        TenantHolder.remove();
    }

}
