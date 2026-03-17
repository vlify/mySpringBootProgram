package com.sky.aspect;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;

import lombok.extern.slf4j.Slf4j;

/**
 * @author sky
 * @create 2024-06-17 16:44
 * @description 切面类,用于实现功能字段自动填充的功能
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

  /**
   * 切入点
   */
  @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
  public void AutoFillPointCut() {
  }

  /**
   * 前置通知,在切入点方法执行之前执行,实现功能字段自动填充的功能
   * 
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   */
  @Before("AutoFillPointCut()")
  public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, SecurityException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    log.info("执行了功能字段自动填充");

    // 获取数据库操作类型
    MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 获取被拦截方法的签名
    AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); // 获取被拦截方法上的注解对象
    OperationType operationType = autoFill.value(); // 获取注解对象中的数据库操作类型

    // 获取被拦截方法的参数列表 -- 实体对象
    Object[] args = joinPoint.getArgs();
    if (null == args || 0 == args.length) {
      return;
    }
    Object entity = args[0]; // 获取第一个参数,即实体对象

    // 准备赋值的数据
    LocalDateTime now = LocalDateTime.now(); // 获取当前时间
    Long currentUserId = BaseContext.getCurrentId(); // 获取当前登录用户的id

    // 根据不同的数据库操作类型,通过反射给实体对象的不同属性赋值
    if (OperationType.INSERT.equals(operationType)) {

      entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class).invoke(entity, now);
      entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class).invoke(entity, currentUserId);
      entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(entity, now);
      entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(entity, currentUserId);

    } else if (OperationType.UPDATE.equals(operationType)) {

      entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class).invoke(entity, now);
      entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class).invoke(entity, currentUserId);

    }
  }
}
