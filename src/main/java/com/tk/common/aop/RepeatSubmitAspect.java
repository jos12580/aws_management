package com.tk.common.aop;

import com.tk.common.annotation.RepeatSubmit;
import com.tk.common.exception.GlobalException;
import com.tk.common.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Aspect
public class RepeatSubmitAspect {

    @Resource
    private RedisTemplate<String,String> redisTemplate;
    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.tk.common.annotation.RepeatSubmit)")
    public void repeatSubmit() {}


    /**
     * ip+类+方法+账号
     */
    @Around("repeatSubmit()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 获取防重复提交注解
        RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
        // 获取注解中的key表达式
        String keyExpression = annotation.key();
        // 解析表达式
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = args[i];
            String parameterName = parameter.getName();
            context.setVariable(parameterName, arg);
        }
//        获取表达式的值
        String key="";
        if (StringUtils.hasText(keyExpression)) {
            key = parser.parseExpression(keyExpression).getValue(context, String.class);
        }
        String ipAddress = IpUtils.getIpAddress(request);
        String url = request.getRequestURI();
        StringJoiner joiner = new StringJoiner(":");
        String redisKey = joiner.add("repeat_submit_key")
                .add(ipAddress)
                .add(url)
                .add(key)
                .toString();
        if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
            redisTemplate.opsForValue().set(redisKey, redisKey, annotation.expireTime(), TimeUnit.SECONDS);
            try {
                //正常执行方法并返回
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                redisTemplate.delete(redisKey);
                throw new Throwable(throwable);
            }
        } else {
            // 抛出异常
            throw new GlobalException("请勿重复请求");
        }
    }



}
