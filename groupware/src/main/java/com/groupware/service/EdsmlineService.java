package com.groupware.service;

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
	
}
