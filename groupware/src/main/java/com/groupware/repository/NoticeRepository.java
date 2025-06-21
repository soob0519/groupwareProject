package com.groupware.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.groupware.entity.NoticeDto;

public interface NoticeRepository extends JpaRepository<NoticeDto,Integer> {
	
	@Query(value = "select * from notice where ntctt like '%' || :search || '%' ",nativeQuery = true)
	List<NoticeDto> searchByTitle(@Param("search") String search);
	
	Page<NoticeDto> findByNtcttContaining(String ntctt, Pageable pageable);
}
