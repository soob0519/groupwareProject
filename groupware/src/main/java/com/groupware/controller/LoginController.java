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
	
	// 컨트롤러 서비스 실행설정
	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}

	// 로그인화면 출력
	@GetMapping("/login")
	public ModelAndView login() {
		
		// 화면 모델 출력
		ModelAndView model = new ModelAndView();
		model.setViewName("login/login");
		return model;
	}
	
	// 로그인 처리
	@PostMapping
	@ResponseBody
	public String login(@RequestParam String userid,@RequestParam String pass,HttpSession session) {
		
		String msg = "";
		
		// 사용자조회
		EmpDto user = loginService.login(userid,pass);
		
		// 로그인 성공 -> 세션 저장
		if(user != null) {	
			
			// 부서명으로 code테이블 ucode에 조회
			String ucode = loginService.getDeptCode(user.getDept());
			
			// 로그인된 사용자정보불러오기
			session.setAttribute("empno",user.getEmpno());		  // 해당 사용자 사원번호 불러오기
			session.setAttribute("userid",user.getUserid()); 	  // 해당 사용자 아이디 불러오기
			session.setAttribute("name",user.getName());      	  // 해당 사용자 이름 불러오기	
			session.setAttribute("dept",ucode);					  // 해당 사용자 부서 불러오기
			session.setAttribute("position",user.getPosition());  // 해당 사용자 직급 불러오기
			
			return "ok";
		}
		
		// 로그인 실패할 시
		else return "fail";
	}
	
	// 로그아웃 처리
	@GetMapping("logout")
	public void logout(HttpSession session) {session.invalidate();}
}