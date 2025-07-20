package com.groupware.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.groupware.entity.EdsmlineDto;
import com.groupware.repository.EdsmlineRepository;

@Service
public class EdsmlineService {
	
	public final EdsmlineRepository edsmlineRepository;
	public EdsmlineService(EdsmlineRepository edsmlineRepository) {
		this.edsmlineRepository = edsmlineRepository;
	}
	public EdsmlineDto save(EdsmlineDto line) {
		return edsmlineRepository.save(line);
	}
	
	public List<EdsmlineDto> findByEdsmnos(List<Integer> edsmnos) {
		return edsmlineRepository.findByEdsmnoIn(edsmnos);
	}
	public List<EdsmlineDto> findByEdsmno(int edsmno) {
		return edsmlineRepository.findByEdsmno(edsmno);
	}
	
	public void deleteByEdsmno(int edsmno) {
		edsmlineRepository.deleteByEdsmno(edsmno);  // 결재선 먼저 삭제1
		
	}
	public List<EdsmlineDto> findByEmpno(int empno) {
		return edsmlineRepository.findByEmpno(empno);
	}
	
}
