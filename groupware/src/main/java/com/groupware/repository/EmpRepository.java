package com.groupware.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Pro.entity.EmpDto;



public interface EmpRepository extends JpaRepository<EmpDto, Integer> {
	 Optional<EmpDto> findByUserid(String userid);
	 
	

	
	 


}
