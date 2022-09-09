package com.kidoneself.aio.common.ide.aspect;


import com.kidoneself.aio.common.core.base.aspect.BaseAspect;
import com.kidoneself.aio.common.core.util.WebUtils;
import com.kidoneself.aio.common.ide.annotation.Ide;
import com.kidoneself.aio.common.ide.exception.IdeException;
import cn.yiidii.pigeon.common.redis.core.RedisOps;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 幂等处理切面
 * <p>
 * 根据谢新的建议对所有参数进行加密检验，提供思路，可以自行扩展
 * DigestUtils.md5Hex(userId + "-" + request.getRequestURL().toString()+"-"+ JSON.toJSONString(request.getParameterMap()));
 * 或 DigestUtils.md5Hex(ip + "-" + request.getRequestURL().toString()+"-"+ JSON.toJSONString(request.getParameterMap()));
 * </p>
 *
 * @author YiiDii Wang
 * @create 2021-04-28 18:56
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnClass(RedisOps.class)
public class IdeAspect extends BaseAspect {

    private final RedissonClient redissonClient;

    private final ThreadLocal<String> MD5_HEX = new ThreadLocal<>();

    /**
     * 锁等待时长
     */
    private static final int LOCK_WAIT_TIME = 5;

    @Pointcut("@annotation(com.kidoneself.aio.common.ide.annotation.Ide)")
    public void watchIde() {
    }

    @Before("watchIde()")
    public void doBefore(JoinPoint joinPoint) {
        Ide ide = getAnnotation(joinPoint, Ide.class);
        if (Objects.isNull(ide)) {
            return;
        }

        // 对所有参数进行md5加密检验
        HttpServletRequest request = WebUtils.getRequest();
        String ip = WebUtils.getIpAddr(request);
        String url = request.getRequestURL().toString();
        String paramStr = JSON.toJSONString(request.getParameterMap());
        String md5Hex = DigestUtils.md5Hex(StringUtils.join(new String[]{ip, url, paramStr}, "-"));
        MD5_HEX.set(md5Hex);
        RLock rLock = redissonClient.getLock(md5Hex);
        boolean target;
        try {
            target = !rLock.tryLock(LOCK_WAIT_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            target = true;
        }
        if (target) {
            log.warn("请求[{}]命中幂等策略, params: {}; ip: {}", url, paramStr, ip);
            throw new IdeException();
        }
    }

    @After("watchIde()")
    public void doAfter(JoinPoint joinPoint) throws Throwable {
        Ide ide = getAnnotation(joinPoint, Ide.class);
        if (Objects.isNull(ide)) {
            return;
        }
        String md5Hex = MD5_HEX.get();
        RLock rLock = redissonClient.getLock(md5Hex);
        rLock.unlock();
    }

}
