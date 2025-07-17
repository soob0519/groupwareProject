package com.groupware.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.groupware.entity.EdsmlineDto;


public interface EdsmlineRepository extends JpaRepository<EdsmlineDto,Integer> {
	
	 // 여러 건용
    List<EdsmlineDto> findByEdsmnoIn(List<Integer> edsmnos);

    // 단건용 (상세조회용)
    List<EdsmlineDto> findByEdsmno(int edsmno);
    
    // 결재선 삭제
	void deleteByEdsmno(int edsmno);
}
