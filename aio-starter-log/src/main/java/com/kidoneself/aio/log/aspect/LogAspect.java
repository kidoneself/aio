package com.kidoneself.aio.log.aspect;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

import com.kidoneself.aio.common.core.base.R;
import com.kidoneself.aio.common.core.base.aspect.BaseAspect;
import com.kidoneself.aio.common.core.util.SpringContextHolder;
import com.kidoneself.aio.common.core.util.TraceUtil;
import com.kidoneself.aio.common.core.util.WebUtils;
import com.kidoneself.aio.log.annotation.Log;
import com.kidoneself.aio.log.event.LogEvent;
import com.kidoneself.aio.log.model.OptLogDTO;
import com.alibaba.fastjson.JSONObject;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 日志切面
 *
 * @author YiiDii Wang
 * @date 2021/4/6 15:03:22
 */
@Slf4j
@Aspect
public class LogAspect extends BaseAspect {

    public static final int MAX_LENGTH = 65535;
    private static final ThreadLocal<OptLogDTO> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 解析SpEL表达式
     */
    ExpressionParser parser = new SpelExpressionParser();

    /**
     * 将方法参数纳入Spring管理
     */
    LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Pointcut("@annotation(com.kidoneself.aio.log.annotation.Log)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint) {
        Log log = this.getAnnotation(joinPoint, Log.class);
        if (!check(log)) {
            return;
        }
        OptLogDTO optLogDTO = buildOptLogDTO(joinPoint, log);
        THREAD_LOCAL.set(optLogDTO);
    }

    /**
     * 异常通知
     *
     * @param joinPoint 端点
     * @param e         e
     */
    @AfterThrowing(pointcut = "pointcut()", throwing = "e")
    public void doAfterThrowable(JoinPoint joinPoint, Throwable e) {
        Log log = this.getAnnotation(joinPoint, Log.class);
        if (check(log)) {
            return;
        }

        OptLogDTO optLogDTO = THREAD_LOCAL.get();
        optLogDTO.setType("EX");
        optLogDTO.setException(ExceptionUtil.stacktraceToString(e, MAX_LENGTH));

        publishEvent(optLogDTO);
    }

    /**
     * 返回通知
     *
     * @param joinPoint 端点
     * @param ret       ret
     */
    @AfterReturning(returning = "ret", pointcut = "pointcut()")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        Log log = this.getAnnotation(joinPoint, Log.class);
        if (!check(log)) {
            return;
        }

        OptLogDTO optLogDTO = THREAD_LOCAL.get();

        String type = log.type();
        if (StrUtil.isNotBlank(type)) {
            optLogDTO.setType(type);
        } else {
            R<?> r = Convert.convert(R.class, ret);
            if (r.getCode() == 0) {
                optLogDTO.setType("OPT");
            } else {
                optLogDTO.setType("EX");
            }
        }
        publishEvent(optLogDTO);
    }

    /**
     * 检查是否需要记录日志
     *
     * @param log Log
     * @return true 需要; false  不需要
     */
    private boolean check(Log log) {
        return Objects.nonNull(log) && log.enabled();
    }

    private OptLogDTO buildOptLogDTO(JoinPoint joinPoint, Log log) {
        HttpServletRequest request = WebUtils.getRequest();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        // 参数
        Object[] args = joinPoint.getArgs();
        String[] parameterNameArr = discoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < parameterNameArr.length; len++) {
            context.setVariable(parameterNameArr[len], args[len]);
        }
        // SpEL
        String content = log.content();
        content = parser.parseExpression(content).getValue(context).toString();

        String ipAddr = WebUtils.getIpAddr(request);
        String location = WebUtils.getLocationByIp(ipAddr);

        return OptLogDTO.builder()
                .traceId(TraceUtil.getTraceId(request))
                .content(content)
                .url(request.getRequestURI())
                .method(request.getMethod())
                .params(JSONObject.toJSONString(args))
                .ip(ipAddr)
                .location(location)
                .startTime(LocalDateTime.now())
                .build();
    }

    /**
     * 发布事件
     *
     * @param optLogDTO optLogDTO
     */
    private void publishEvent(OptLogDTO optLogDTO) {
        optLogDTO.setEndTime(LocalDateTime.now());
        optLogDTO.setConsumingTime(optLogDTO.getStartTime().until(optLogDTO.getEndTime(), ChronoUnit.MILLIS));
        SpringContextHolder.publishEvent(new LogEvent(optLogDTO));
        THREAD_LOCAL.remove();
    }
}
