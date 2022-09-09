package com.kidoneself.aio.common.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 链路追踪配置
 *
 * @author YiiDii Wang
 * @create 2021-04-06 22:47
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties("pigeon.trace")
public class PigeonTraceProperties {

    /**
     * 是否开启日志链路追踪
     */
    private Boolean enabled = false;

    /**
     * 是否启用获取IP地址
     */
    private Boolean ip = false;

    /**
     * 是否启用增强模式
     */
    private Boolean enhance = false;

}
