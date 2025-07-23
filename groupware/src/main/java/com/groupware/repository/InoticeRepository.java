package com.groupware.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.groupware.entity.InoticeDto;

public interface InoticeRepository extends JpaRepository<InoticeDto,Integer> {
	
	// 전체공지사항 검색기능 + 필수공지사항 -> 일반공지사항 순으로 역순정렬 (제목구분)
	@Query(value = " select * from Inotice where intctt like '%' || :search || '%' "
				 + " order by case when intcca = '필수' then 0 else 1 end, intcrd desc", 
		   countQuery  = "  select count(*) from Inotice where intctt like '%' || :search || '%' ", nativeQuery = true)
	
	// 검색에 따른 데이터 개수에 따라 페이지 재 정렬
	Page<InoticeDto> findByIntcttContaining(@Param("search")String search,Pageable pageable);
	
	// 전체공지사항 부서분류 + 필수공지사항 -> 일반공지사항 순으로 역순정렬 (제목구분)
	@Query(value = " select * from Inotice where deptno like %:deptno% "
				 + " order by case when intcca = '필수' then 0 else 1 end, intcrd desc ",
		   countQuery = " select count(*) from inotice where deptno like %:deptno% ",nativeQuery = true)
	
	// 부서분류에 따른 데이터 개수에 따라 페이지 재 정렬
	Page<InoticeDto> findByDeptnoContaining(String deptno, Pageable pageable);
	
	// 유형(필수) 값 개수 계산 SQL
	@Query(value = " select count(*) from Inotice where intcca = '필수' ", nativeQuery = true)
	long countByIntcca();
}