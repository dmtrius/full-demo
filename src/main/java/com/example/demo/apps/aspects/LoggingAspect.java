package com.example.demo.apps.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* com.example.demo.apps.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Calling: {}", joinPoint.getSignature());
    }
    @AfterReturning(pointcut = "execution(* com.example.demo.apps.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        log.info("Completed: {}", joinPoint.getSignature());
    }
}
