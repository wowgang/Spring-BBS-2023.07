package com.ys.sbbs.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

/*
 * Servlet Filter
 */
@Component
public class SbbsFilter extends HttpFilter implements Filter {
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();
		session.setMaxInactiveInterval(10 * 3600); 	// 10 시간
		
		String uri = httpRequest.getRequestURI();
		if (uri.contains("board"))	// nav bar active주기 =>  uri에 board가 포함이면
			session.setAttribute("menu", "board");	// 메뉴값을 board로 바꾸겠다
		else if (uri.contains("user"))
			session.setAttribute("menu", "user");
		else
			session.setAttribute("menu", "");
		
		String sessionUid = (String) session.getAttribute("sessUid");
		String[] urlPatterns = { "/board", "/aside", "/file", "user/liset", "/user/update", "/user/delete" };	// 로그인이 되어야 필요한것
		
		for (String routing: urlPatterns) {
			if (uri.contains(routing)) {
				if (sessionUid == null || sessionUid.equals(""))
					httpResponse.sendRedirect("/sbbs/user/login");
				break;
			}
		} 
		
		chain.doFilter(request, response);
		
	}//doFilter

}//SbbsFilter
