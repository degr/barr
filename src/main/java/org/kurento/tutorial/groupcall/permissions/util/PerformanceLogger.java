package org.kurento.tutorial.groupcall.permissions.util;

import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class PerformanceLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceLogger.class);

    @SneakyThrows
    @Around("@annotation(org.kurento.tutorial.groupcall.permissions.util.ExecutionTime)||execution(* org.kurento.tutorial.groupcall.websocket.command..*(..))")
    public Object logPerformance(ProceedingJoinPoint point) {
        String signature = point.getSignature().toShortString();
        long start = System.currentTimeMillis();
        LOGGER.info("Method {} execution started at: {}", signature, LocalDateTime.now());
        Object proceed = point.proceed();
        long end = System.currentTimeMillis();
        LOGGER.info("Method {} execution lasted: {}", signature, end - start);
        LOGGER.info("Method {} execution ended at: {}", signature, LocalDateTime.now());
        return proceed;
    }
}