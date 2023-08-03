package com.ys.sbbs.controller;

import java.time.LocalDateTime;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExceptionHandlingController implements ErrorController{
	private final String ERROR_404_PAGE_PATH = "error/error404";
	private final String ERROR_500_PAGE_PATH = "error/error500";
	private final String ERROR_ETC_PAGE_PATH = "error/error"; // 그외 에러
	
	@GetMapping("/error")
	public String handleError(HttpServletRequest req, Model model) {
		// 에러 코드 획득
		Object status = req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		
		// 에러 코드에 대한 상태 정보
		HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(status.toString()));
		
		if (status != null) {
			int statusCode = Integer.valueOf(status.toString());
			String timestamp = LocalDateTime.now().toString().substring(0,19).replace("T", " ");
			
			// 404error
			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				model.addAttribute("code", status.toString());
				model.addAttribute("msg", httpStatus.getReasonPhrase());
//				model.addAttribute("timestamp", LocalDateTime.now());
				model.addAttribute("timestamp", timestamp);
				return ERROR_404_PAGE_PATH;
			}
			
			// 500 error
			if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				model.addAttribute("code", status.toString());
				model.addAttribute("msg", httpStatus.getReasonPhrase());
//				model.addAttribute("timestamp", LocalDateTime.now());
				model.addAttribute("timestamp", timestamp);
				return ERROR_500_PAGE_PATH;
			}
		}
		return ERROR_ETC_PAGE_PATH;
	}
}
