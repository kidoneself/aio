package com.kidoneself.aio.common.core.config;


import com.kidoneself.aio.common.core.util.DozerUtils;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.spring.DozerBeanMapperFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * Dozer spring auto configuration.
 * <p>
 * ConditionalOnClass：该注解的参数对应的类必须存在，否则不解析该注解修饰的配置类；
 * ConditionalOnMissingBean：该注解表示，如果存在它修饰的类的bean，则不需要再创建这个bean；
 * <p>
 * http://dozer.sourceforge.net/documentation/usage.html
 * http://www.jianshu.com/p/bf8f0e8aee23
 *
 * @author zuihou
 * @date 2017-11-23 16:27
 */
@Configuration
@ConditionalOnClass({DozerBeanMapperFactoryBean.class, Mapper.class})
@ConditionalOnMissingBean(Mapper.class)
public class DozerAutoConfiguration {

    private static final ResourcePatternResolver PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();

    @Bean
    public DozerUtils getDozerUtils(Mapper mapper) {
        return new DozerUtils(mapper);
    }

    /**
     * Creates default Dozer mapper
     *
     * @return Dozer mapper
     * @throws IOException if there is an exception during initialization.
     */
    @Bean
    public DozerBeanMapperFactoryBean dozerMapper() throws IOException {
        DozerBeanMapperFactoryBean factoryBean = new DozerBeanMapperFactoryBean();
        // 暂定扫描classpath*:dozer/*.dozer.xml
        String[] mappingFiles = new String[]{"classpath*:dozer/*.dozer.xml"};
        factoryBean.setMappingFiles(Stream.of(Optional.ofNullable(mappingFiles).orElse(new String[0]))
                .flatMap(location -> Stream.of(getResources(location)))
                .toArray(Resource[]::new));
        return factoryBean;
    }

    private Resource[] getResources(String location) {
        try {
            return PATTERN_RESOLVER.getResources(location);
        } catch (IOException var3) {
            return new Resource[0];
        }
    }
}
