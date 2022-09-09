package com.kidoneself.aio.common.knife4j.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

import static com.kidoneself.aio.common.knife4j.config.Knife4jProperties.PREFIX;

/**
 * 文档属性
 *
 * @author YiiDii Wang
 * @create 2021-02-15 11:25
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = PREFIX)
public class Knife4jProperties {

    @PostConstruct
    public void init() {
        log.info("=== Knife4jProperties init...");
    }

    public static final String PREFIX = "aio.knife4j";

    /**
     * 标题
     */
    private String title = "在线接口文档";
    /**
     * 分组
     */
    private String group = "";
    /**
     * 描述
     */
    private String description = "pigeon-cloud 在线接口文档";
    /**
     * 版本
     */
    private String version = "1.0";
    /**
     * 许可证
     */
    private String license = "";
    /**
     * 许可证URL
     */
    private String licenseUrl = "";
    /**
     * 服务条款URL
     */
    private String termsOfServiceUrl = "";

    private Contact contact = new Contact();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Contact {
        /**
         * 联系人
         **/
        private String name = "";
        /**
         * 联系人url
         **/
        private String url = "";
        /**
         * 联系人email
         **/
        private String email = "";
    }

}
