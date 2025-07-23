package com.groupware.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.groupware.entity.ViewerDto;
import com.groupware.repository.ViewerRepository;

@Service
public class ViewerService {
	
	public final ViewerRepository viewerRepository;
	public ViewerService(ViewerRepository viewerRepository ) {
		this.viewerRepository = viewerRepository;
	
	}
	//1
	public List<ViewerDto> findByEdsmno(int edsmno) {
		return viewerRepository.findByEdsmno(edsmno);
	}
	
	public ViewerDto save(ViewerDto dto) {
        return viewerRepository.save(dto);
    }
	
	public List<ViewerDto> findByEmpno(int empno) {
        return viewerRepository.findByEmpno(empno);
    }
	public void deleteByEdsmno(int edsmno) {
		viewerRepository.deleteByEdsmno(edsmno);
	}
	
}
