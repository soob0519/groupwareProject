package com.groupware.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.groupware.entity.CodeDto;


public interface CodeRepository extends JpaRepository<CodeDto,String> {
	
	// 부서명으로 코드값 가져오기1
	@Query("SELECT c.ucode FROM CodeDto c WHERE c.ncode = :deptName AND c.pcode = 'B200'")
	String findUcodeByNcode(@Param("deptName") String deptName);
	
	// 부서 목록 가져오기
	@Query("SELECT c FROM CodeDto c WHERE c.pcode = 'B200' ORDER BY c.dorder ASC")
	List<CodeDto> findAllActiveDepartments();
	
	//부서코드로 부서명찾기 (다연)
	Optional<CodeDto> findByNcode(String ncode);
	//부서명 자동완성 (다연)
	List<CodeDto> findByNcodeContainingAndState(String ncode, String state);
	
	
	
}