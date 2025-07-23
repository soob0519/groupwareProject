package com.groupware.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.groupware.entity.ViewerDto;

public interface ViewerRepository extends JpaRepository<ViewerDto,Integer> {
	
	List<ViewerDto> findByEdsmno(int edsmno);
	
	//1
	List<ViewerDto> findByEmpno(int empno);
	
	void deleteByEdsmno(int edsmno);
}
