package ru.kpfu.itis.group400.stashkov.aop;

import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.group400.stashkov.service.metrics.Counter;

@Aspect
@Component
@AllArgsConstructor
public class CountAspect {

    private final Counter counter;

    @Pointcut("@annotation(Count)")
    public void count() {}

    @Around("count()")
    public Object add(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object result = joinPoint.proceed();
            counter.incrementSuccess(joinPoint.getSignature().getName());
            return result;
        } catch (Throwable throwable) {
            counter.incrementFail(joinPoint.getSignature().getName());
            throw throwable;
        }
    }

}
