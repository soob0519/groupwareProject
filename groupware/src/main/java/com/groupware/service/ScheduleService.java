package com.groupware.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ZEC_Project.entity.ScheduleDto;
import com.ZEC_Project.repository.ScheduleRepository;

@Service
public class ScheduleService {
	
	public final ScheduleRepository scheduleRepository;
	
	public ScheduleService(ScheduleRepository scheduleRepository) {
		this.scheduleRepository = scheduleRepository;
	}
	
	
	// 달력 출력
	public List<Map> calList(String sddate) {
		return scheduleRepository.calList(sddate);
	}
	
	
	// 일정등록
	public ScheduleDto scheduleSave(ScheduleDto dto) {
		return scheduleRepository.save(dto);
	}
	


	
	
}
















