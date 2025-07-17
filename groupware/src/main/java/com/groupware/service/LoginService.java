package com.groupware.service;

import org.springframework.stereotype.Service;
import com.groupware.entity.EmpDto;
import com.groupware.repository.CodeRepository;
import com.groupware.repository.LoginRepository;

@Service
public class LoginService {

	// 리퍼지토리 설정 (로그인정보)
	public final LoginRepository loginRepository;

	// 전체컨트롤러 서비스 실행 설정
	public LoginService(LoginRepository loginRepository,CodeRepository codeRepository) {
		this.loginRepository = loginRepository;
	}
	
	// Repository 에서 Dto 값 가져오기 (로그인 감지 여부 설정) 
	public EmpDto login(String userid,String pass) {
		return loginRepository.findByUseridAndPass(userid,pass).orElse(null);
	}
}