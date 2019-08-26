package com.zengshi.butterfly.core.security;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect 
public class SystemArchitecture { 
  /** 
   * A Join Point is defined in the action layer where the method needs 
   * a permission check. 
   */ 
   @Pointcut("@annotation(com.zengshi.butterfly.core.annotation.Security)") 
   public void userAccess() {} 
   
}
