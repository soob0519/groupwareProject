package com.groupware.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.groupware.entity.ScheduleDto;
import com.groupware.repository.ScheduleRepository;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<ScheduleDto> findScheduleByYearMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return scheduleRepository.findByStartdateBetween(start, end);
    }

    public ScheduleDto save(ScheduleDto dto) {
        return scheduleRepository.save(dto);
    }
    
    public ScheduleDto findById(int scheno) {
        return scheduleRepository.findById(scheno).orElse(null);
    }
    
    public boolean deleteById(int scheno) {
    	if(scheduleRepository.existsById(scheno)) {
    		scheduleRepository.deleteById(scheno);
    		return true;
    	}
    	return false;
    }
}