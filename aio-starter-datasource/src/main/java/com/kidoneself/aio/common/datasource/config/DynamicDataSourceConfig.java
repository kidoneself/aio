package com.kidoneself.aio.common.datasource.config;

import com.kidoneself.aio.common.core.util.SpringContextHolder;
import com.kidoneself.aio.common.datasource.context.DynamicDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * DynamicDataSourceConfig
 *
 * @author YiiDii Wang
 * @create 2021-05-25 13:21
 */
@EnableTransactionManagement
@Configuration
@RequiredArgsConstructor
public class DynamicDataSourceConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean("primary")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primary() {
        return DataSourceBuilder.create().build();
    }

    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("primary", primary());
        // 将 primary 数据源作为默认指定的数据源
        dynamicDataSource.setDefaultDataSource(primary());
        // 将 primary 和 slave 数据源作为指定的数据源
        dynamicDataSource.setDataSources(dataSourceMap);
        return dynamicDataSource;
    }

    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactoryBean() throws Exception {
        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setGlobalConfig(getMybatisPlusGlobalConfig());
        // 插件
        Interceptor[] plugins = new Interceptor[1];
        plugins[0] = mybatisPlusInterceptor();
        sessionFactory.setPlugins(plugins);
        // 配置数据源, 此处配置为关键配置, 如果没有将dynamicDataSource作为数据源则不能实现切换
        sessionFactory.setDataSource(dynamicDataSource());
        // 枚举包
        String typeEnumsPage = applicationContext.getEnvironment().getProperty("mybatis-plus.type-enums-package");
        sessionFactory.setTypeEnumsPackage(typeEnumsPage);
        // 扫描Model
        // sessionFactory.setTypeAliasesPackage("com.example.demo.*.*.entity,com.example.demo.model");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 扫描映射文件
        sessionFactory.setMapperLocations(resolver.getResources("classpath*:mapper/*.xml"));
        return sessionFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        // 配置事务管理, 使用事务时在方法头部添加@Transactional注解即可
        return new DataSourceTransactionManager(dynamicDataSource());
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    private GlobalConfig getMybatisPlusGlobalConfig() {
        GlobalConfig globalConfig = GlobalConfigUtils.defaults();
        // mybatis-plus全局配置设置元数据对象处理器为自己实现的那个
        MetaObjectHandler metaObjectHandler =applicationContext.getBean(MetaObjectHandler.class);
        globalConfig.setMetaObjectHandler(metaObjectHandler);
        return globalConfig;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}