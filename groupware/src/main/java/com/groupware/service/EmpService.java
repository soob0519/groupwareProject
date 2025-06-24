package com.groupware.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.groupware.entity.EmpDto;
import com.groupware.repository.EmpRepository;


@Service
public class EmpService {
	
	public final EmpRepository empRepository;
	public EmpService(EmpRepository empRepository) {
		this.empRepository = empRepository;
	}
	
	/**
	 *   등록 처리
	 */
	public EmpDto save(EmpDto dto) {
		
		return empRepository.save(dto);
	}
	
	/**
	 *   삭제 처리
	 */
	public boolean deleteById(Integer empno) {
		if (empRepository.existsById(empno)) {
			empRepository.deleteById(empno);
			return true;
		}
		return false;
	}
	
	/**
	 *  목록 출력
	 */
	public Page<EmpDto>  list(int page,int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("empno").descending());
		return empRepository.findAll(pageable);
	}

	/**
	 *  상세정보
	 */
	public EmpDto detail(int empno) {
		return empRepository.findById(empno).orElse(null);
	}
}
