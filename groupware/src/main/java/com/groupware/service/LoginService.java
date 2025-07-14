package com.groupware.service;

import org.springframework.stereotype.Service;

import com.groupware.entity.EmpDto;
import com.groupware.repository.CodeRepository;
import com.groupware.repository.LoginRepository;

@Service
public class LoginService {

	// 리퍼지토리 설정 (로그인정보,코드정보)
	public final LoginRepository loginRepository;
	public final  CodeRepository  codeRepository;
	
	// 전체컨트롤러 서비스 실행 설정
	public LoginService(LoginRepository loginRepository,CodeRepository codeRepository) {
		this.loginRepository = loginRepository;
		this. codeRepository =  codeRepository;
	}
	
	// Repository 에서 Dto 값 가져오기 (로그인 감지 여부 설정) 
	public EmpDto login(String userid,String pass) {
		return loginRepository.findByUseridAndPass(userid,pass).orElse(null);
	}
	
	// Repository 에서 부서코드 값 가져오기 (해당로그인 사용자의 부서정보와 코드)
	public String getDeptCode(String deptName) {
	    String ucode = codeRepository.findUcodeByNcode(deptName);
	    return ucode;
	}
}