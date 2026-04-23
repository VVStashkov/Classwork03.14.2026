package ru.kpfu.itis.group400.stashkov.aop;

import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.group400.stashkov.service.metrics.BenchMarker;

import java.time.Duration;
import java.time.Instant;

@Aspect
@Component
@AllArgsConstructor
public class BenchMarkAspect {

    private final BenchMarker benchMarker;

    @Pointcut("@annotation(BenchMark)")
    public void benchmark() {}

    @Around("benchmark()")
    public Object bench(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant start = Instant.now();
        try {
            return joinPoint.proceed();
        } finally {
            Instant end = Instant.now();
            double durationSeconds = Duration.between(start, end).toNanos() / 1_000_000_000.0;
            benchMarker.saveNewTime(joinPoint.getSignature().getName(), durationSeconds);
        }
    }
}
