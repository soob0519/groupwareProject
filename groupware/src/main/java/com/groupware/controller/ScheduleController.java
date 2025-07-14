package com.groupware.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.groupware.service.ScheduleService;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {
	
	public final ScheduleService scheduleService;
	
	public ScheduleController(ScheduleService scheduleService) {
		this.scheduleService = scheduleService;
	}
	
	
	/**
	 * 일정등록화면
	 */
	@GetMapping("/list")
	public ModelAndView frame() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/schedule/calendar");
		return model;
	}
	
	
}














