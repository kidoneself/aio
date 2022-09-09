package com.kidoneself.aio.common.datasource.config;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.kidoneself.aio.common.core.util.SpringContextHolder;
import com.kidoneself.aio.common.datasource.context.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YiiDii Wang
 * @create 2021-05-25 13:14
 */
@Slf4j
@Configuration
public class DynamicDataSourceInit {

    @Bean
    public void initDataSource() {
        log.info("======初始化动态数据源=====");
        DynamicDataSource dynamicDataSource = SpringContextHolder.getBean("dynamicDataSource");
        HikariDataSource master = SpringContextHolder.getBean("primary");
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", master);

        List<TenantDatasource> dsList = getDatasource();
        log.info("simulate datasource list: {}", JSONObject.toJSON(dsList));
        for (TenantDatasource tenantInfo : dsList) {
            log.info(tenantInfo.toString());
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setDriverClassName(tenantInfo.getDriverClassName());
            dataSource.setJdbcUrl(tenantInfo.getJdbcUrl());
            dataSource.setUsername(tenantInfo.getUsername());
            dataSource.setPassword(tenantInfo.getPassword());
            dataSource.setDataSourceProperties(master.getDataSourceProperties());
            dataSourceMap.put(tenantInfo.getTenantId(), dataSource);
        }
        // 设置数据源
        dynamicDataSource.setDataSources(dataSourceMap);
        /**
         * 必须执行此操作，才会重新初始化AbstractRoutingDataSource 中的 resolvedDataSources，也只有这样，动态切换才会起效
         */
        dynamicDataSource.afterPropertiesSet();
    }

    private List<TenantDatasource> getDatasource() {
        TenantDatasource ds01 = new TenantDatasource(1L, "1", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/pigeon?useUnicode=true&characterEncoding=UTF-8&userSSL=false&serverTimezone=UTC&zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "123456");
        TenantDatasource ds02 = new TenantDatasource(2L, "2", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost.cn:3306/pigeon?useUnicode=true&characterEncoding=UTF-8&userSSL=false&serverTimezone=UTC&zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "123456");
        return Lists.newArrayList(ds01, ds02);
    }

    @Data
    @AllArgsConstructor
    public static class TenantDatasource {

        private Long id;
        private String tenantId;
        private String driverClassName;
        private String jdbcUrl;
        private String username;
        private String password;

    }
}