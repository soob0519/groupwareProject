package com.groupware.service;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.groupware.entity.DeptDto;
import com.groupware.repository.DeptRepository;

@Service
public class DeptService {
	
	public final DeptRepository deptRepository;
	public DeptService(DeptRepository deptRepository) {
		this.deptRepository = deptRepository;
	}
	
	/**
	 *   등록 처리
	 */
	public DeptDto save(DeptDto dto) {
		
		return deptRepository.save(dto);
	}
	
	/**
	 *   삭제 처리
	 */
	public boolean deleteById(Integer deptno) {
		if (deptRepository.existsById(deptno)) {
			deptRepository.deleteById(deptno);
			return true;
		}
		return false;
	}
	
	/**
	 *  목록 출력
	 */
	public Page<DeptDto>  list(int page,int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("deptno").descending());
		return deptRepository.findAll(pageable);
	}

	/**
	 *  상세정보
	 */
	public DeptDto detail(int deptno) {
		return deptRepository.findById(deptno).orElse(null);
	}
	
	/**
	 *  deptno 101부터
	 */
	public int deptnoNew() {
		return deptRepository.findByNative();
	}
	
}
