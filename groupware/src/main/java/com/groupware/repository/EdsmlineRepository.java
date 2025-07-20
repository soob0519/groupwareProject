package com.groupware.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.groupware.entity.EdsmlineDto;


public interface EdsmlineRepository extends JpaRepository<EdsmlineDto,Integer> {
	
	 // 여러 건용1
    List<EdsmlineDto> findByEdsmnoIn(List<Integer> edsmnos);

    // 단건용 (상세조회용)
    List<EdsmlineDto> findByEdsmno(int edsmno);
    
    // 결재선 삭제
	void deleteByEdsmno(int edsmno);
	
	// 사원이 가지고 있는 결제라인 찾는 거
	List<EdsmlineDto> findByEmpno(int empno);
}
