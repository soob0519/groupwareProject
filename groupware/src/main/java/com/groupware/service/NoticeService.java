package com.groupware.service;

import org.springframework.stereotype.Service;
import com.jpaproject.repository.NoticeRepository;

@Service
public class NoticeService {

	// 리퍼지토리 설정
	public final NoticeRepository noticeRepository;
	
	// 컨트롤러 서비스 실행 설정
	public NoticeService(NoticeRepository noticeRepository) {this.noticeRepository = noticeRepository;}
}
