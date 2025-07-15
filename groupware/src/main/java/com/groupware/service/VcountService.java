package com.groupware.service;

import org.springframework.stereotype.Service;

import com.groupware.repository.VcountRepository;


@Service
public class VcountService {
	
	public final VcountRepository vcountRepository;
	public VcountService(VcountRepository vcountRepository) {
		this.vcountRepository = vcountRepository;
	
	}
}
