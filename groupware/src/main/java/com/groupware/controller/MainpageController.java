package com.groupware.controller;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.ZEC_Project.entity.ScheduleDto;
import com.ZEC_Project.service.ScheduleService;

@RestController
@RequestMapping("/index")
public class MainpageController {
	
	public final ScheduleService scheduleService;
	
	public MainpageController(ScheduleService scheduleService) {
		this.scheduleService = scheduleService;
	}
	
	
	
	/**
	 * 메인화면 출력
	 */
	@GetMapping
	public ModelAndView index(String year, String month) {
		
		ModelAndView model = new ModelAndView();
		
		Calendar cal = Calendar.getInstance();
		
		//현시점 날짜
		int yy = cal.get(Calendar.YEAR);
		int mm = cal.get(Calendar.MONTH);
		int dd = cal.get(Calendar.DATE);
		
		int tyy = yy;
		int tmm = mm;
		int tdd = dd;
		
		if(year != null && !year.equals("")) {
			yy = Integer.parseInt(year);
		}
		if(month != null && !month.equals("")) {
			mm = Integer.parseInt(month);
			mm--;
		}
		
		cal.set(yy,mm,1);
		
		int lastDay = cal.getActualMaximum(Calendar.DATE);
		
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		
		List<Map> list = scheduleService.calList(yy+"-"+(mm+1));
		System.out.print("listSize ::::::::::" + list.size());
		for(int i=0; i<list.size(); i++) {
			
		}
		
		model.setViewName("/mainpage/index");
		
		model.addObject("menu","schedule");
		model.addObject("list",list);
		model.addObject("lastDay",lastDay);
		model.addObject("dayOfWeek",dayOfWeek);
		model.addObject("yy",yy);
		model.addObject("mm",mm);
		model.addObject("dd",dd);
		model.addObject("tyy",tyy);
		model.addObject("tmm",tmm);
		model.addObject("tdd",tdd);
				
		return model;
	}
	
	
	/**
	 * 일정 저장
	 */
	@PostMapping
	public String scheduleSave(ScheduleDto dto) {
		
		String msg = "1";
		
		
		
		return msg;
	}
	
	
	
	
	
}


























