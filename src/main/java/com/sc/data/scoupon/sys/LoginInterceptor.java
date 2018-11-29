package com.sc.data.scoupon.sys;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sc.data.scoupon.stat.SysStat;
 
public class LoginInterceptor implements HandlerInterceptor {
 
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    Object user = request.getSession().getAttribute(SysStat.USER_SESSION_KEY);
    if (user == null) {
      System.out.println("尚未登录，调到登录页面");
//      response.sendRedirect("/fanli/unLogin.do");
      request.getRequestDispatcher("/fanli/unLogin.do").forward(request, response);
      return false;
    }
    return true;
  }
 
  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    System.out.println("postHandle");
  }
 
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    System.out.println("afterCompletion");
  }
 
}