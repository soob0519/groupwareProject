package com.groupware.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.groupware.entity.InoticeDto;
import com.groupware.entity.MailDto;
import com.groupware.service.InoticeService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/Inotice")
public class InoticeController {
	
	// 서비스 설정
	private final InoticeService inoticeService;
	
	// 컨트롤러 서비스 실행설정
	public InoticeController(InoticeService inoticeService) {
		this.inoticeService = inoticeService;
	}

	// 사용자 전체공지사항 목록화면 출력
	@GetMapping("/UserList")
	public ModelAndView userList(@RequestParam(defaultValue = "1") int indexpage, 
							 	 @RequestParam(defaultValue =  "") String search,
							 	 @RequestParam(defaultValue =  "") String deptno) {
		
		// 화면 모델 출력
		ModelAndView  model = new ModelAndView();
		
		// 총 데이터베이스(전체공지사항 항목) 개수
		Long total = inoticeService.count();
		
		// 한 페이지당 보여줄 공지사항 데이터(항목) 개수
		int pageData = 10;  
		
		// 1page를 원하면 -> 0번세팅, 검색,분류 포함 페이징 처리
	    Page<InoticeDto> page = inoticeService.list(indexpage -1, pageData, search, deptno);
	    
		// 화면 출력 시작번호 = (총 데이터개수 -(현재페이지번호 - 1) * 출력단위)
		int startPageRownum = (int)(page.getTotalElements() - page.getNumber() * pageData);
		
		// 1화면에 출력되는 페이지 출력 범위 ex = 5 : 1 2 3 4 5
		int pageSize = 5;
		
		// 출력되는 현재 페이지
		int currentPage = (indexpage - 1) / pageSize;
		
		// 페이지 계산처리
		int startPage = currentPage * pageSize + 1;
		int   endPage = Math.min(startPage + pageSize - 1, page.getTotalPages());
		
		model.addObject("search", search);
		model.addObject("deptno", deptno);
		model.addObject("indexpage", indexpage);
		model.addObject("currentPage", indexpage); // 현재 페이지 강조 표시용(색처리 대상)
		model.addObject("plist",page.getContent());
		model.addObject("startPage", startPage);
		model.addObject("endPage", endPage);
		model.addObject("startPageRownum",startPageRownum);
		model.addObject("ptotal",page.getTotalElements());
		model.addObject("ptotalPage",page.getTotalPages());
		model.setViewName("notice/userInoticeList");
		
		return model;
	}
	
	// 관리자 전체공지사항 목록화면 출력
	@GetMapping("/AdminList")
	public ModelAndView adminList(@RequestParam(defaultValue = "1") int indexpage, 
							 	  @RequestParam(defaultValue =  "") String search,
							 	  @RequestParam(defaultValue =  "") String deptno) {
			
		// 화면 모델 출력
		ModelAndView  model = new ModelAndView();
		
		// 총 데이터베이스(전체공지사항 항목) 개수
		Long total = inoticeService.count();
		
		// 한 페이지당 보여줄 공지사항 데이터(항목) 개수
		int pageData = 10;  
		
		// 1page를 원하면 -> 0번세팅, 검색,분류 포함 페이징 처리
	    Page<InoticeDto> page = inoticeService.list(indexpage -1, pageData, search, deptno);
	    
		// 화면 출력 시작번호 = (총 데이터개수 -(현재페이지번호 - 1) * 출력단위)
		int startPageRownum = (int)(page.getTotalElements() - page.getNumber() * pageData);
		
		// 1화면에 출력되는 페이지 출력 범위 ex = 5 : 1 2 3 4 5
		int pageSize = 5;
		
		// 출력되는 현재 페이지
		int currentPage = (indexpage - 1) / pageSize;
		
		// 페이지 계산처리
		int startPage = currentPage * pageSize + 1;
		int   endPage = Math.min(startPage + pageSize - 1, page.getTotalPages());
		
		model.addObject("search", search);
		model.addObject("deptno", deptno);
		model.addObject("indexpage", indexpage);
		model.addObject("currentPage", indexpage); // 현재 페이지 강조 표시용(색처리 대상)
		model.addObject("plist",page.getContent());
		model.addObject("startPage", startPage);
		model.addObject("endPage", endPage);
		model.addObject("startPageRownum",startPageRownum);
		model.addObject("ptotal",page.getTotalElements());
		model.addObject("ptotalPage",page.getTotalPages());
		model.setViewName("notice/adminInoticeList");
		
		return model;
	}
	
	// 사용자 전체공지사항 상세보기화면 출력
	@GetMapping("User/{user}/{Intcno}")
	public ModelAndView userDetail(@PathVariable int user, @PathVariable int Intcno,HttpSession session) {
		
		// 모델 설정
		ModelAndView model = new ModelAndView();
		
		//상세정보 서비스
		InoticeDto dto = inoticeService.detail(Intcno);
		
		// user가 1일경우 상세보기로 이동
		if(user == 1) {
		
			// 상세보기 내용 줄바꿈,공백처리,특수문자처리 
			String cont = dto.getIntcct();
			cont.replace(" ", "&nbsp;");
			cont.replace(">", "&gt");
			cont.replace("<", "&lt");
			cont.replace("\n","<br>");
			dto.setIntcct(cont);
			
			//조회수 증가 (관리자용은 증가X 증가는 오직 사용자만)
			inoticeService.saveHits(Intcno);
			
			// 상세보기 링크설정
			model.setViewName("notice/userInoticeDetail");
		}
		
		// user가 2일경우 메일작성으로 이동
		else if(user == 2) {
			
			String   name = (String) session.getAttribute("name");
	        String userId = (String) session.getAttribute("userid");
	        if (userId == null) {
	            return new ModelAndView("redirect:login/login");
	        }
	        
	        MailDto draft = new MailDto();
	        draft.setSenderId(userId); // 혹시 몰라 미리 세팅
	        
	        // 메일작성 링크설정
	        ModelAndView mv = new ModelAndView("notice/userInoticeMailWrite");
	        
	        mv.addObject("draft", draft);
	        mv.addObject("replyMode", false); 
	        mv.addObject("dto",dto); // 메일내용에 들어갈 공지사항내용 출력하기 위해
	        
	        return mv;
		}
		
		// 상세보기 화면에 출력
		model.addObject("dto",dto);
		
		return model;
	}
	
	// 관리자 전체공지사항 상세보기화면,수정하기화면 출력
	@GetMapping("Admin/{admin}/{Intcno}")
	public ModelAndView adminDetail(@PathVariable int admin, @PathVariable int Intcno,HttpSession session) {
		
		// 모델 설정
		ModelAndView model = new ModelAndView();
		
		//상세정보 서비스
		InoticeDto dto = inoticeService.detail(Intcno);
		
		// 유형(필수 값) 조회
		long imp  = inoticeService.countByIntcca();
		
		// 수정화면에 유형값이 이미 필수일 경우 imp 카운트 제외
		if ( dto != null && "필수".equals(dto.getIntcca())) imp -= 1;
		
		// admin이 1일경우 상세보기로 이동
		if(admin == 1) {
			
			// 상세보기 내용 줄바꿈,공백처리,특수문자처리 
			String cont = dto.getIntcct();
			cont.replace(" ", "&nbsp;");
			cont.replace(">", "&gt");
			cont.replace("<", "&lt");
			cont.replace("\n","<br>");
			dto.setIntcct(cont);
			
			// 상세보기 링크설정
			model.setViewName("notice/adminInoticeDetail");
		}
		
		// admin이 2일경우 메일작성으로 이동
		else if(admin == 2) {
			
			String   name = (String) session.getAttribute("name");
	        String userId = (String) session.getAttribute("userid");
	        if (userId == null) {
	            return new ModelAndView("redirect:login/login");
	        }
	        
	        MailDto draft = new MailDto();
	        draft.setSenderId(userId); // 혹시 몰라 미리 세팅
	        
	        // 메일작성 링크설정
	        ModelAndView mv = new ModelAndView("notice/adminInoticeMailWrite");
	        
	        mv.addObject("draft", draft);
	        mv.addObject("replyMode", false); 
	        mv.addObject("dto",dto); // 메일내용에 들어갈 공지사항내용 출력하기 위해
	        
	        return mv;
		}

		// adimn이 3일경우 수정하기로 이동
		else if(admin == 3) {
			// 수정하기 링크설정
			model.setViewName("notice/adminInoticeModify");
		}
		
		// 상세보기,수정하기 서비스 화면에 출력
		model.addObject("dto",dto);
		
		// 유형(필수) 값 출력
		model.addObject("imp",imp);
		
		return model;
	}
		
	// 관리자 전체공지사항 등록화면 출력 
	@GetMapping("/AdminWrite")
	public ModelAndView AdminWrite() {
			
		// 유형(필수 값) 조회
		long imp = inoticeService.countByIntcca();  
		
		// 화면 모델 출력
		ModelAndView model = new ModelAndView();
		
		model.addObject("imp",imp);
		model.setViewName("notice/adminInoticeWrite");
		
		return model;
	}
		
	// 관리자 공지사항 등록,수정,삭제 서비스
	@PostMapping
	public String notice(InoticeDto dto1) throws Exception {	
		
		String 	  	msg = "ok";
		InoticeDto dto2 = inoticeService.notice(dto1);
		if(dto2 == null) msg = "fail";  
		return msg;
	}	
}