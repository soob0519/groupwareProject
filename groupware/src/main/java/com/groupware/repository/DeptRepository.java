package com.groupware.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.groupware.entity.DeptDto;


public interface DeptRepository extends JpaRepository<DeptDto,Integer> {
	
	@Query(value="select nvl(max(deptno),100)+1 from dept",nativeQuery=true)
	int findByNative();


}