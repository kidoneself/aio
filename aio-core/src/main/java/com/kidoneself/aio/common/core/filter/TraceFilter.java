package com.kidoneself.aio.common.core.filter;

import com.kidoneself.aio.common.core.properties.PigeonTraceProperties;
import com.kidoneself.aio.common.core.util.TraceUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 日志链路追踪过滤器
 *
 * @author YiiDii Wang
 * @create 2021-04-06 22:49
 */
@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(Filter.class)
public class TraceFilter extends OncePerRequestFilter {

    @Autowired
    private PigeonTraceProperties traceProperties;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return !traceProperties.getEnabled();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String traceId = TraceUtil.getTraceId(request);
//            if (StringUtils.isBlank(traceId)) {
//                WebUtils.renderJson(response, R.failed("缺少trace-id头"));
//            }
//            log.info("request url: {} ; traceId: {}", request.getRequestURL(), traceId);
            TraceUtil.mdcTraceId(traceId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }

    }
}
