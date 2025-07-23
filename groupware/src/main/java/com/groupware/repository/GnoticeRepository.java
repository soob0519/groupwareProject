package com.groupware.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.groupware.entity.GnoticeDto;

public interface GnoticeRepository extends JpaRepository<GnoticeDto,Integer> {
	
	// 일반공지사항 검색기능 (제목구분)
	@Query(value = "select * from Gnotice where gntctt like '%' || :search || '%' order by gntcrd desc ", nativeQuery = true)
	
	// 검색에 따른 데이터 개수에 따라 페이지 재 정렬
	Page<GnoticeDto> findByGntcttContaining(@Param("search")String search, Pageable pageable);
	
	// 일반공지사항 분류기능 (부서구분)
	@Query(value = "select * from Gnotice where deptno like %:deptno% order by gntcrd desc ", nativeQuery = true)
	
	// 부서분류에 따른 데이터 개수에 따라 페이지 재 정렬
	Page<GnoticeDto> findByDeptnoContaining(String deptno, Pageable pageable);
}