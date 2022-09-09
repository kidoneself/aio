package com.kidoneself.aio.log.prop;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 操作日志配置
 *
 * @author YiiDii Wang
 * @date 2021/11/10 9:48:50
 */
@Data
@ConfigurationProperties(prefix = OptLogProperties.PREFIX)
@NoArgsConstructor
public class OptLogProperties {

    public static final String PREFIX = "pigeon.log";

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 日志存储类型
     */
    private OptLogType type = OptLogType.LOGGER;
}
