package com.kidoneself.aio.common.feign.annotation;

import com.kidoneself.aio.common.core.constant.BizConstant;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.*;

/**
 *  Feign注解
 * 
 * @author YiiDii Wang
 * @date 2021/4/12 14:58:55
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients(value = BizConstant.BUSINESS_PACKAGE)
public @interface EnablePigeonFeign {
}
