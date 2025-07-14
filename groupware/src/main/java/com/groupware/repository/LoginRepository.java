package com.groupware.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.groupware.entity.EmpDto;

public interface LoginRepository extends JpaRepository<EmpDto,Integer> {
	
	// Dto에서 로그인 검증 대상 아이디 비번, 부서 따오기
	Optional<EmpDto>findByUseridAndPass(String userid, String pass);
}
