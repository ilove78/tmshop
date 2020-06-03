package com.how2java.tmall.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.how2java.tmall.pojo.User;

public class LoginInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		HttpSession session = request.getSession();
		String contextPath = session.getServletContext().getContextPath();
//		System.out.println("session="+session+"**contextPath="+contextPath);
		String[] requireAuthPages = new String[]{
                "buy",
                "alipay",
                "payed",
                "cart",
                "bought",
                "confirmPay",
                "orderConfirmed",
                 
                "forebuyone",
                "forebuy",
                "foreaddCart",
                "forecart",
                "forechangeOrderItem",
                "foredeleteOrderItem",
                "forecreateOrder",
                "forepayed",
                "forebought",
                "foreconfirmPay",
                "foreorderConfirmed",
                "foredeleteOrder",
                "forereview",
                "foredoreview"
                 
        };
		String uri = request.getRequestURI();
//		System.out.println("uri1="+uri);
		 uri = StringUtils.remove(uri, contextPath+"/");
		 String page = uri;
//		 System.out.println("page="+page);
		 if(begingWith(page, requireAuthPages)) {
			 Subject subject = SecurityUtils.getSubject();//使用shiro进行过滤器登录验证
//			 User user = (User) session.getAttribute("user");
//			 System.out.println("user="+user);
			 if(!subject.isAuthenticated()) {
				 response.sendRedirect("login");
				 return false;
			 }
		 }
		return true;
	}
	
	private boolean begingWith(String page, String[] requiredAuthPages) {
        boolean result = false;
        for (String requiredAuthPage : requiredAuthPages) {
            if(StringUtils.startsWith(page, requiredAuthPage)) {
                result = true; 
                break;
            }
        }
        return result;
    }

}
