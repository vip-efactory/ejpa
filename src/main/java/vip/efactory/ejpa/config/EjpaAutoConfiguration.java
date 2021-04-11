package vip.efactory.ejpa.config;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Description:根据属性初始化配置类，可以在此处实例化一些配置类
 *
 *
 * @author dbdu
 */
@Configuration
@Slf4j
@Getter
@ComponentScans(value = {@ComponentScan("vip.efactory.ejpa.*")})
//@EnableJpaRepositories(repositoryBaseClass = DataFilterRepositoryImpl.class)
public class EjpaAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("init ejpa start...");
    }

}
