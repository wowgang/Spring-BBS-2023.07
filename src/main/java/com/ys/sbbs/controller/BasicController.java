package com.ys.sbbs.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

//restcontroller = > String 리턴해줌 (json)
//controller => jsp
@Controller
@RequestMapping("/basic")
public class BasicController {
		// jsp파일 부를때
	// 엑세스 경로 localhost:8080/sbbs/basic/basic1
		@RequestMapping("/basic1")
		public String basic1() {
			// application.properties에 prefix=/WEB-INF/view/, suffix=.jsp
			// /WEB-INF/view/basic/basic1.jsp
			return "basic/basic1";
		}
		
		@ResponseBody
		@RequestMapping("/basic2")
		public String basic2() {
			//문자열을 출력
			return "<h1>문자열을 웹화면으로 보낼때 @ResponseBody 를 사용</h1>";
		}
		
		@RequestMapping("/basic3")
		public String basic3(Model model) {
			model.addAttribute("filename", "basic3.jsp");
			model.addAttribute("message", "Model 객체를 통해서 데이터가 전달됩니다.");
			List<String> fruits = new ArrayList<>();
			fruits.add("망고"); fruits.add("수박"); fruits.add("복숭아");
			model.addAttribute("fruits", fruits);
			return "basic/basic3";
		}
		
		@ResponseBody
		@RequestMapping("/basic4")
		public String basic4(HttpServletRequest req) {
			String id = req.getParameter("id");
			// http://localhost:8080/sbbs/basic/basic4?id=spring
			return "<h1>파라메터로 받은 id 값은 " + id + " 입니다.<br>" +
					"기존 방식의 HttpServletRequest 로 받을 수 있습니다.</h1>";
		}
		
		@ResponseBody
		@RequestMapping("/basic5")
		public String basic5(int num, @RequestParam(name="id", defaultValue="spring") String id) {
			// http://localhost:8080/sbbs/basic/basic5?num=3&id=spring
			return "<h1>파라메터로 받은 num 값은 " + num + " 입니다.<br>" +
					"파라메터로 받은 id 값은 " + id + " 입니다</h1>";
			// String 값을 integer로 변환이 자동으로 됨
			// deaultvalue 설정 @RequestParam(name="id", defaultValue="spring") => http://localhost:8080/sbbs/basic/basic5?num=3
		}
		
		// Servlet에서는 불가능한 방법 
		// responsesendredirect한꼴
		@RequestMapping("/basic6/{num}/{id}")
		public String basic6(@PathVariable int num, @PathVariable String id) {
			// http://localhost:8080/sbbs/basic/basic6/3/spring
			return "redirect:/sbbs/basic/basic5?num=" + num + "&id=" + id;

		}
		
		@GetMapping("/basic7")
		public String basic7(HttpServletRequest req, Model model) {
			HttpSession session = req.getSession();
			session.setAttribute("sessionMsg", "세션을 통한 메세지 전달");
			model.addAttribute("modelMsg", "모델을 통한 메세지 전달");
			return "basic/basic7";
		}
		
		@GetMapping("/basic8")
		public String basic8get() {
			return "basic/basic8";
		}
		
		@ResponseBody
		@PostMapping("/basic8")
		public String basic8post(String id, String pwd) {
			return "<h1>id: " + id + ", pwd: " + pwd + "</h1>";
		}
		
		@GetMapping("/calc")
		public String getCalculate() {
			return "basic/calc";
		}
		@PostMapping("/calc")
	    public String postCalculate(@RequestParam int num1, @RequestParam int num2, @RequestParam String operator, Model model) {
	        int result = 0;
	        switch (operator) {
	            case "+":
	                result = num1 + num2;
	                break;
	            case "-":
	                result = num1 - num2;
	                break;
	            case "*":
	                result = num1 * num2;
	                break;
	            case "/":
	                result = num1 / num2;
	                break;
	        }
	        model.addAttribute("result", result);
	        return "basic/calc"; // Thymeleaf 템플릿 파일 이름
	    }
}
