package com.groupware.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.groupware.entity.NoticeDto;

public interface NoticeRepository extends JpaRepository<NoticeDto,Integer> {
	
	// 공지사항 검색기능 + 필수공지사항 -> 일반공지사항 순으로 역순정렬
	@Query(value = "select * from notice where ntctt like '%' || :search || '%' "
				 + "order by case when ntcca = '필수' then 0 else 1 end, ntcno desc", 
		   countQuery  = "select count(*) from notice where ntctt like '%' || :search || '%'", nativeQuery = true)
	
	// 검색에 따른 데이터 개수에 따라 페이지 재 정렬
	Page<NoticeDto> findByNtcttContaining(@Param("search")String search, Pageable pageable);
}