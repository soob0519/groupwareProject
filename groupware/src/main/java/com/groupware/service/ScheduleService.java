package com.groupware.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ZEC_Project.entity.ScheduleDto;
import com.ZEC_Project.repository.ScheduleRepository;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Map> calList(String rdate) {
        return scheduleRepository.calList(rdate);
    }

    public ScheduleDto save(ScheduleDto dto) {
        return scheduleRepository.save(dto);
    }
}