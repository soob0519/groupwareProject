package com.groupware.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.groupware.entity.PositionDto;
import com.groupware.repository.PositionRepository;

@Service
public class PositionService {
	
	public final PositionRepository positionRepository;
	public PositionService(PositionRepository positionRepository) {
		this.positionRepository = positionRepository;
	}
	
	/**
	 *   등록 처리
	 */
	public PositionDto save(PositionDto dto) {
		
		return positionRepository.save(dto);
	}
	
	/**
	 *   삭제 처리
	 */
	public boolean deleteById(Integer posno) {
		if (positionRepository.existsById(posno)) {
			positionRepository.deleteById(posno);
			return true;
		}
		return false;
	}
	
	/**
	 *  목록 출력
	 */
	public Page<PositionDto>  list(int page,int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("posno").descending());
		return positionRepository.findAll(pageable);
	}

	/**
	 *  상세정보
	 */
	public PositionDto detail(int posno) {
		return positionRepository.findById(posno).orElse(null);
	}

	public int posnoNew() {
		return positionRepository.findByNative();
	}
}
