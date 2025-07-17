package com.groupware.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Pro.entity.DfileDto;

public interface DfileRepository extends JpaRepository<DfileDto, Integer> {

    // 전체 리스트 (정렬만)
    List<DfileDto> findAllByOrderByFilenoDesc();

    // 페이징 정렬
    Page findAllByOrderByFilenoDesc(Pageable pageable); 
    
    //삭제된항목 안보이는 리스트
    Page<DfileDto> findByFiledelete(DfileDto.DeleteStatus filedelete, Pageable pageable);

    
    

}

