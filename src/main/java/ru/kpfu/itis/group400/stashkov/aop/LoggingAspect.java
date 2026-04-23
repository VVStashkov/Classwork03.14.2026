package ru.kpfu.itis.group400.stashkov.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    public static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

//    @Pointcut("execution(* ru.kpfu.itis.group400.stashkov..*.*(..)) && !within(ru.kpfu.itis.group400.stashkov.dto..*)" +
//            "&& !within(ru.kpfu.itis.group400.stashkov.config..*)")
//    public void logExecution() {
//    }

    @Pointcut("@annotation(Loggable)")
    public void logAnnotated() {}

    @Around("logAnnotated()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        logger.debug("Start execution: {}, {} ", className, methodName);
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e){
            throw new RuntimeException(e);
        }
        logger.debug("Finish execution: {}, {} ", className, methodName);
        return result;
    }

}
