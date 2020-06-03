package com.how2java.tmall.web;

import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForeLogoutController {

	@GetMapping(value="/forelogout")
    public String logout(HttpSession session) {
		 Subject subject = SecurityUtils.getSubject();
		    if(subject.isAuthenticated())
		        subject.logout();
        return "redirect:home";
    }
}
