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

/**
 * AOP-аспект для логирования времени выполнения методов слоя сервисов.
 * Перехватывает все public-методы классов из пакета com.credit.service
 * (включая все вложенные подпакеты).
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

  /**
   * Pointcut: любые методы любых классов из пакета com.credit.service
   * и всех его подпакетов.
   */
  @Pointcut("execution(* com.credit.service..*(..))")
  public void serviceLayer() {
  }

  /**
   * Around-совет: засекает время выполнения метода, логирует вход/выход,
   * в случае исключения — тоже фиксирует время до падения.
   */
  @Around("serviceLayer()")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String className = signature.getDeclaringType().getSimpleName();
    String methodName = signature.getName();

    if (log.isDebugEnabled()) {
      log.debug("--> {}.{}() called with args={}",
          className, methodName, Arrays.toString(joinPoint.getArgs()));
    }

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

  /**
   * Дополнительный совет на случай, если где-то исключение проскочит мимо
   * Around (например, из-за проксирования). Пишет ERROR-лог именно в
   * отдельный error-файл (см. logback-spring.xml).
   */
  @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
  public void logException(Throwable ex) {
    log.error("Exception in service layer: {} — {}",
        ex.getClass().getSimpleName(), ex.getMessage());
  }
}