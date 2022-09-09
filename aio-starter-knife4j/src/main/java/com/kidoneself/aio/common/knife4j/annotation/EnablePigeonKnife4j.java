package com.kidoneself.aio.common.knife4j.annotation;

import com.kidoneself.aio.common.knife4j.config.Knife4jConfig;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.annotation.*;

/**
 * @author YiiDii Wang
 * @create 2021-02-15 11:47
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableSwagger2
@EnableKnife4j
@Import({Knife4jConfig.class})
public @interface EnablePigeonKnife4j {
}
