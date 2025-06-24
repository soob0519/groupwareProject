package com.groupware.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.groupware.entity.EmpDto;



public interface EmpRepository extends JpaRepository<EmpDto,Integer> {


}