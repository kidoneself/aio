package com.kidoneself.aio.log.config;

import com.kidoneself.aio.log.aspect.LogAspect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 日志配置
 *
 * @author YiiDii Wang
 * @create 2021-04-12 22:23
 */
@Slf4j
@RequiredArgsConstructor
public class LogConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LogAspect logAspect() {
        return new LogAspect();
    }

}
