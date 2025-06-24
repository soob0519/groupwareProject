package com.groupware.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.groupware.entity.GcodeDto;
import com.groupware.repository.GcodeRepository;

@Service
public class GcodeService {
	
	public final GcodeRepository gcodeRepository;
	public GcodeService(GcodeRepository gcodeRepository) {
		this.gcodeRepository = gcodeRepository;
	}

	/**
	 *   등록 처리
	 */
	public GcodeDto save(GcodeDto dto) {
		
		return gcodeRepository.save(dto);
	}
	
	/**
	 *   삭제 처리
	 */
	public boolean deleteById(Integer cdno) {
		if (gcodeRepository.existsById(cdno)) {
			gcodeRepository.deleteById(cdno);
			return true;
		}
		return false;
	}
	
	/**
	 *  목록 출력
	 */
	public Page<GcodeDto>  list(int page,int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("cdno").descending());
		return gcodeRepository.findAll(pageable);
	}

	/**
	 *  상세정보
	 */
	public GcodeDto detail(int cdno) {
		return gcodeRepository.findById(cdno).orElse(null);
	}
	
}
