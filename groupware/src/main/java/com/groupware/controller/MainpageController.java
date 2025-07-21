package com.groupware.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.groupware.entity.InoticeDto;
import com.groupware.entity.ScheduleDto;
import com.groupware.service.InoticeService;
import com.groupware.service.ScheduleService;

@Controller
@RequestMapping("/index")
public class MainpageController {
	
	public final ScheduleService scheduleService;
	private final InoticeService inoticeService;
	public MainpageController(ScheduleService scheduleService,
								InoticeService inoticeService) {
		this.scheduleService = scheduleService;
		this.inoticeService = inoticeService;
	}
	
	
	/**
	 * 메인화면 출력
	 */
	@GetMapping
	public ModelAndView index(String year, String month
							,@RequestParam(defaultValue = "1") int indexpage, 
		 	 				  @RequestParam(defaultValue =  "") String search,
		 	 				  @RequestParam(defaultValue =  "") String deptno) {
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


























