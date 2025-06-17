package com.groupware.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.groupware.service.NoticeService;

@RestController
@RequestMapping("/notice")
public class NoticeController {
	
	// 서비스 설정
	private final NoticeService noticeService;
	
	// 컨트롤러 서비스 실행설정
	public NoticeController(NoticeService noticeService) {
		this.noticeService = noticeService;
	}
}
