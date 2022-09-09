package com.kidoneself.aio.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志注解
 *
 * @author YiiDii Wang
 * @date 2021/4/6 14:31:31
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

    /**
     * 是否启用, 默认启用
     *
     * @return boolean
     */
    boolean enabled() default true;

    /**
     * 操作日志内容, 支持SpEL表达式
     *
     * @return content
     */
    String content();

    /**
     * 类型
     *
     * @return 类型
     */
    String type() default "";

}
