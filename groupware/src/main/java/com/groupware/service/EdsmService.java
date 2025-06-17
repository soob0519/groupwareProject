package com.groupware.service;

import org.springframework.stereotype.Service;

import com.groupware.repository.EdsmRepository;

@Service
public class EdsmService {
	
	public final EdsmRepository edsmRepository;
	public EdsmService(EdsmRepository edsmRepository) {
		this.edsmRepository = edsmRepository;
	}
	
}
