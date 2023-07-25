package com.ys.sbbs.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/sbbs/rest")
@org.springframework.web.bind.annotation.RestController

public class RestController {
	
	// ResController에서 @ResponseBody가 생략이 가능함
	@RequestMapping("/hello") 
	public String hello() {
		return "<h1>Hello World!!!</h1>";
	}
}