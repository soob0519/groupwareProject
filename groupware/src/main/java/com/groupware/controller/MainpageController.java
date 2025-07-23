package com.groupware.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.groupware.entity.EdsmDto;
import com.groupware.entity.EdsmlineDto;
import com.groupware.entity.EmpDto;
import com.groupware.entity.InoticeDto;
import com.groupware.entity.ScheduleDto;
import com.groupware.entity.ViewerDto;
import com.groupware.service.CodeService;
import com.groupware.service.EdsmService;
import com.groupware.service.EdsmlineService;
import com.groupware.service.EmpService;
import com.groupware.service.InoticeService;
import com.groupware.service.ScheduleService;
import com.groupware.service.ViewerService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/index")
public class MainpageController {
	
	public final ScheduleService scheduleService;
	private final InoticeService inoticeService;
	private final EdsmService edsmService;
	private final EdsmlineService edsmlineService;
	private final ViewerService viewerService;
	private final EmpService empService;
	private final CodeService codeService;
	public MainpageController(ScheduleService scheduleService,
								InoticeService inoticeService,
								EdsmService edsmService,
								EdsmlineService edsmlineService,
								ViewerService viewerService,
								EmpService empService,
								CodeService codeService) {
		this.scheduleService = scheduleService;
		this.inoticeService = inoticeService;
		this.edsmService = edsmService;
		this.edsmlineService = edsmlineService;
		this.viewerService = viewerService;
		this.empService = empService;
		this.codeService = codeService;
	}
	
	
	/**
	 * 메인화면 출력
	 */
	@GetMapping
	public ModelAndView index(String year, String month
							,@RequestParam(defaultValue = "1") int indexpage, 
		 	 				  @RequestParam(defaultValue =  "") String search,
		 	 				  @RequestParam(defaultValue =  "") String deptno
		 	 				,HttpSession session) {
	    
		// 세션 추가
		int empno = (int) session.getAttribute("empno");
		
		ModelAndView model = new ModelAndView();

	    Calendar cal = Calendar.getInstance();

	    // 기본값: 현재 년월
	    int yy = cal.get(Calendar.YEAR);
	    int mm = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH는 0부터 시작해서 +1 필요

	    if (year != null && !year.isEmpty()) {
	        yy = Integer.parseInt(year);
	    }
	    if (month != null && !month.isEmpty()) {
	        mm = Integer.parseInt(month);
	    }

	    cal.set(yy, mm - 1, 1);

	    int lastDay = cal.getActualMaximum(Calendar.DATE);
	    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

	    
	    // 공지관련
	    List<ScheduleDto> list = scheduleService.findScheduleByYearMonth(yy, mm);
 		Long total = inoticeService.count();
 		int pageData = 10;  
 	    Page<InoticeDto> page = inoticeService.list(indexpage -1, pageData, search, deptno);
 		int startPageRownum = (int)(page.getTotalElements() - page.getNumber() * pageData);
 		
 		/**
		 * 메인화면 결재목록 띄우기 시작
		 */
        Page<EdsmDto> basePage = edsmService
                .findDocsByEmpno(empno, null, null, null,
                        PageRequest.of(0, 200, Sort.by("wdate").descending()));
        List<EdsmDto> baseDocs = new ArrayList<>(basePage.getContent());
        
        List<EdsmlineDto> myLinesAll = edsmlineService.findByEmpno(empno);
        Set<Integer> myLineDocIds = new HashSet<>();
        for (EdsmlineDto l : myLinesAll) {
            myLineDocIds.add(l.getEdsmno());
        }

        List<ViewerDto> myViewerList = viewerService.findByEmpno(empno);
        Set<Integer> viewerDocIds = new HashSet<>();
        for (ViewerDto v : myViewerList) {
            viewerDocIds.add(v.getEdsmno());
        }

        Set<Integer> docIdSet = new HashSet<>();
        for (EdsmDto d : baseDocs) docIdSet.add(d.getEdsmno());
        docIdSet.addAll(myLineDocIds);
        docIdSet.addAll(viewerDocIds);

        List<EdsmDto> allDocs = docIdSet.stream()
                .map(id -> edsmService.findById(id))
                .filter(Objects::nonNull)
                .toList();

        List<EdsmDto> inProgressDocs = allDocs.stream()
                .filter(d -> "F60001".equals(d.getEdst()) || "F60002".equals(d.getEdst()))
                .sorted((a, b) -> b.getWdate().compareTo(a.getWdate()))
                .toList();

        List<Integer> inProgressIds = inProgressDocs.stream()
                .map(EdsmDto::getEdsmno)
                .toList();
        List<EdsmlineDto> linesForInProgress = inProgressIds.isEmpty()
                ? List.of()
                : edsmlineService.findByEdsmnos(inProgressIds);

        List<EdsmDto> myTurnDocs = inProgressDocs.stream()
                .filter(doc -> {
                    int docId = doc.getEdsmno();
                    List<EdsmlineDto> docLines = linesForInProgress.stream()
                            .filter(l -> l.getEdsmno() == docId)
                            .toList();

                    List<EdsmlineDto> waiting = docLines.stream()
                            .filter(l -> "C30001".equals(l.getEdst()))
                            .toList();
                    if (waiting.isEmpty()) return false;

                    int minEdpro = waiting.stream()
                            .mapToInt(EdsmlineDto::getEdpro)
                            .min().orElse(Integer.MAX_VALUE);

                    return waiting.stream()
                            .anyMatch(l -> l.getEmpno() == empno && l.getEdpro() == minEdpro);
                })
                .sorted((a, b) -> b.getWdate().compareTo(a.getWdate()))
                .toList();
        
        // 내가 기안하고 진행중인거 필터
        List<EdsmDto> myDraftInProgress = inProgressDocs.stream()
                .filter(d -> d.getEmpno() == empno)
                .toList();
        
        List<EmpDto> empList = empService.findAll();
        Map<Integer,String> empNameMap = empList.stream()
            .collect(Collectors.toMap(EmpDto::getEmpno, EmpDto::getName));
        
        model.addObject("approveList", myDraftInProgress);
        model.addObject("myTurnDocs", myTurnDocs);
        model.addObject("empNameMap", empNameMap);
		/**
		 * 메인화면 결재목록 띄우기 끝
		 */
		
 		
 		model.setViewName("/index/index");
 		
 		// 공지관련
 		model.addObject("search", search);
 		model.addObject("deptno", deptno);
 		model.addObject("indexpage", indexpage);
 		model.addObject("plist",page.getContent());
 		model.addObject("startPageRownum",startPageRownum);
 		model.addObject("ptotal",page.getTotalElements());   
 		
 		
	    model.addObject("menu", "schedule");
	    model.addObject("list", list);
	    model.addObject("lastDay", lastDay);
	    model.addObject("dayOfWeek", dayOfWeek);
	    model.addObject("yy", yy);
	    model.addObject("mm", mm - 1); // 뷰에서는 0-based 월을 기대하므로 -1
	    model.addObject("dd", cal.get(Calendar.DATE));
	    model.addObject("tyy", Calendar.getInstance().get(Calendar.YEAR));
	    model.addObject("tmm", Calendar.getInstance().get(Calendar.MONTH));
	    model.addObject("tdd", Calendar.getInstance().get(Calendar.DATE));

	    return model;
	}
	
	
	/**
	 * 일정 저장
	 */
	@PostMapping("/scheSave")
	@ResponseBody
	public String scheSave(ScheduleDto dto) {
	    try {
	        ScheduleDto dto1 = scheduleService.save(dto);
	        if(dto1 == null) {
	            return "2"; // 저장 실패
	        }
	        return "1"; // 저장 성공
	    } catch (Exception e) {
	        e.printStackTrace(); // 서버 로그 확인용
	        return "0"; // 예외 발생
	    }
	}
	
	
	/**
	 * 수정처리
	 */
	@PostMapping("/update")
	@ResponseBody
	public String updateSchedule(ScheduleDto dto) {
		ScheduleDto unchangedData = scheduleService.findById(dto.getScheno());
		
		String msg = "1";
		
		if(unchangedData == null) {
			msg = "0";	// 수정데이터 없음
		}
		
	    // 필요한 필드만 업데이트
	    unchangedData.setSchetitle(dto.getSchetitle());
	    unchangedData.setSche_chk(dto.getSche_chk());
	    unchangedData.setCal_chk(dto.getCal_chk());
	    unchangedData.setStartdate(dto.getStartdate());
	    unchangedData.setEnddate(dto.getEnddate());
	    unchangedData.setStarttime(dto.getStarttime());
	    unchangedData.setEndtime(dto.getEndtime());
	    unchangedData.setWrtnm(dto.getWrtnm());
	    unchangedData.setSchecont(dto.getSchecont());
	    unchangedData.setParticipant(dto.getParticipant());
	    unchangedData.setSharer(dto.getSharer());
		
	    ScheduleDto updateData = scheduleService.save(unchangedData);
	    
	    if(updateData == null) {
	    	msg = "2";	// 업뎃실패
	    }
	    
		return msg;
	}
	
	
	@PostMapping("/delete")
	@ResponseBody
	public String deleteSchedule(@RequestParam("scheno") int scheno) {
		boolean deleteData = scheduleService.deleteById(scheno);
		
		String msg = "";
		if(deleteData) {
			msg = "1";
		} else {
			msg = "2";
		}
		return msg;
	}
	
	
	
	@GetMapping("/detail")
	@ResponseBody
	public ScheduleDto detail(@RequestParam("scheno") int scheno) {
	    return scheduleService.findById(scheno);
	}
}


























