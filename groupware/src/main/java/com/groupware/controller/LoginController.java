package com.groupware.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.groupware.entity.EmpDto;
import com.groupware.service.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("login")
public class LoginController {

	//서비스설정
	private final LoginService loginService;
	
	@GetMapping
	public String login() {
		
	   return "/login/login";
	}
	
	
	// 컨트롤러 서비스 실행설정
	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}
	
	// 로그인 처리
	@PostMapping
	@ResponseBody
	public String login(@RequestParam String userid,@RequestParam String pass,HttpSession session) {
		
		String msg = "";
		
		// 사용자조회
		EmpDto user  = loginService.login(userid,pass);
		
		// 부서명으로 code테이블 ucode에 조회
		String ucode = loginService.getDeptCode(user.getDept());
		
		// 로그인 성공 -> 세션 저장
		if(user != null) {	
			
			// 회원출력
			session.setAttribute("user",user);
			
			// 회원 해당부서 코드출력
			session.setAttribute("ucode",ucode);
			
			return "ok";
		}
		
		// 로그인 실패할 시
		else return "fail";
	}
	
	// 로그아웃 처리
	@GetMapping("logout")
	public void logout(HttpSession session) {session.invalidate();}
}