package com.groupware.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.groupware.entity.EmpDto;



public interface EmpRepository extends JpaRepository<EmpDto,Integer> {
	
	// 다연
	Optional<EmpDto> findByUserid(String userid);
	// 다연
	List<EmpDto> findByNameContaining(String query);
	// 다연
	List<EmpDto> findByDept(String dept);

	// 이름검색
	Page<EmpDto> findByNameContainingIgnoreCase(String name, Pageable pageable);
	// 부서코드 검색
	Page<EmpDto> findByDept(String dept, Pageable pageable);
	
	Page<EmpDto> findByState(String state, Pageable pageable);

	Page<EmpDto> findByNameContainingIgnoreCaseAndState(String name, String state, Pageable pageable);

	Page<EmpDto> findByDeptAndState(String dept, String state, Pageable pageable);

}
