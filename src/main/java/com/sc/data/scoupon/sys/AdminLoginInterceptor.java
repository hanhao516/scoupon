package com.sc.data.scoupon.sys;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import redis.clients.jedis.Jedis;

import com.sc.data.scoupon.stat.SysStat;
import com.sc.data.scoupon.utils.RedisUtil;

public class AdminLoginInterceptor implements HandlerInterceptor {
 
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	    Object user = null;
	 	 Cookie[] cookies = request.getCookies();
	  	if (null==cookies) {
		 	 System.out.println("没有cookie==============");
	 	 } else {
		 	 for (Cookie cookie : cookies) {
				 if(cookie.getName().equals(SysStat.OA_Session))
					 user = cookie.getValue();
		 	 }
	 	 }
	    if (user==null) {
	      System.out.println("尚未登录，调到登录页面");
	      request.getRequestDispatcher("/fanli/unLogin.do").forward(request, response);
	      return false;
	    }else {
			String OAJedisSessionId = user.toString();
			Jedis jedis = RedisUtil.getJedis();
			String user_name = jedis.get(OAJedisSessionId);
			RedisUtil.returnResource(jedis);
			if(StringUtils.isBlank(user_name)){
				System.out.println("session 失效");
				request.getRequestDispatcher("/fanli/unLogin.do").forward(request, response);
				return false;
			}
		}
	  for (Cookie cookie : cookies) {
		  if(cookie.getName().equals(SysStat.OA_Session)){
			  cookie.setMaxAge(30*60);
		      cookie.setPath("/");
		      response.addCookie(cookie);
		  }
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