package vip.efactory.ejpa.config;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Description:根据属性初始化配置类，同时定义对应切点和增强
 * 含有注解的方法为切入点，方法拦截器为增强
 *
 * @author dbdu
 * @date 18-7-26 上午9:39
 */
@Configuration
@Slf4j
@Getter
@ComponentScans(value = {@ComponentScan("vip.efactory.ejpa.*")})
public class EjpaAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("init ejpa start...");
    }

}
