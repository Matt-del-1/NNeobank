package com.credit.aspect;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

  @Pointcut("execution(* com.credit.service..*(..))")
  public void serviceLayer() {
  }

  @Around("serviceLayer()")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String className = signature.getDeclaringType().getSimpleName();
    String methodName = signature.getName();

    long start = System.currentTimeMillis();
    try {
      Object result = joinPoint.proceed();
      long elapsed = System.currentTimeMillis() - start;
      log.info("<-- {}.{}() executed in {} ms", className, methodName, elapsed);
      return result;
    } catch (Throwable ex) {
      long elapsed = System.currentTimeMillis() - start;
      log.warn("xxx {}.{}() failed after {} ms: {} — {}",
          className, methodName, elapsed,
          ex.getClass().getSimpleName(), ex.getMessage());
      throw ex;
    }
  }
}