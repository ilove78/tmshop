package com.how2java.tmall.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.OrderItemService;

public class OtherInterceptor implements HandlerInterceptor {
	@Autowired CategoryService categoryService;
    @Autowired OrderItemService orderItemService;
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3)
			throws Exception {
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("user");
		int  cartTotalItemNumber = 0;
		if(null!=user) {
			List<OrderItem> orderItem = orderItemService.listByUser(user);
			for(OrderItem oi:orderItem) {
				cartTotalItemNumber+=oi.getNumber();
			}
		}
		List<Category> category = categoryService.list();
		String contextPath = request.getServletContext().getContextPath();
		request.getServletContext().setAttribute("categories_below_search", category);
		session.setAttribute("cartTotalItemNumber", cartTotalItemNumber);
		request.getServletContext().setAttribute("contextPath", contextPath);

	}

	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

}
