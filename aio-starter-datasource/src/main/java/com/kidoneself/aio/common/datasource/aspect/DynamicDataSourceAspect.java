package com.kidoneself.aio.common.datasource.aspect;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.kidoneself.aio.common.datasource.context.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * 动态数据源切面
 * 这里order一定要小于tx:annotation-driven的order，即先执行DynamicDataSourceAspectAdvice切面，再执行事务切面，才能获取到最终的数据源
 *
 * @author YiiDii Wang
 * @create 2021-05-24 20:18
 */

@Slf4j
@Aspect
@Component
@Order(-10)
//@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DynamicDataSourceAspect {

    @Around("execution(* com.kidoneself.*.*.controller.*.*(..))")
    public Object doAround(ProceedingJoinPoint jp) throws Throwable {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Object result = null;
        try {
            HttpServletRequest request = sra.getRequest();
            String tenantId = request.getHeader("tenantId");
            if (StrUtil.isEmpty(tenantId)) {
                tenantId = request.getParameter("tenantId");
            }

            log.info("当前租户Id: {}", tenantId);
            if (!StrUtil.isEmpty(tenantId)) {
                DynamicDataSourceContextHolder.setDataSourceKey(tenantId);
                result = jp.proceed();
            } else {
                result = R.failed("查询失败，当前租户信息未取到，请联系技术专家！");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result = R.failed("系统异常，请联系技术专家！");
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
        return result;
    }
}
