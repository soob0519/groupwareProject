package com.groupware.service;

import com.groupware.repository.EdsmRepository;

public class EdsmService {
	
	public final EdsmRepository edsmRepository;
	public EdsmService(EdsmRepository edsmRepository) {
		this.edsmRepository = edsmRepository;
	}
	
}
