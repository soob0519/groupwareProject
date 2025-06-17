package com.groupware.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jpaproject.entity.NoticeDto;

public interface NoticeRepository extends JpaRepository<NoticeDto,Integer> {}
