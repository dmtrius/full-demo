package com.example.demo.apps.aspects;

import lombok.Builder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MetricsAspect {
    @Around("execution(* com.example.demo.apps.*(..))")
    public Object timeExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.nanoTime() - start;
            Metrics.builder()
                    .signature(joinPoint.getSignature().getName())
                    .duration(duration)
                    .build().print();
        }
    }
}

@Builder
class Metrics {
    private String signature;
    long duration;

    public void print() {
        System.out.println(signature + ": " + duration);
    }
}
