package com.groupware.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.groupware.entity.CodeDto;
import com.groupware.repository.CodeRepository;


@Service
public class CodeService {
	
	public final CodeRepository codeRepository;
	public CodeService(CodeRepository codeRepository) {
		this.codeRepository = codeRepository;
	}
	
	/**
	 *  목록 출력
	 */
	public List<CodeDto>  list() {
		return codeRepository.findAll();
	}
}
