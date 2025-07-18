package com.groupware.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.groupware.entity.InoticeDto;
import com.groupware.repository.InoticeRepository;

@Service
public class InoticeService {

	// 리퍼지토리 설정
	public final InoticeRepository inoticeRepository;
	
	// 전체컨트롤러 서비스 실행 설정
	public InoticeService(InoticeRepository inoticeRepository) {
		this.inoticeRepository = inoticeRepository;
	}
	
	// 전체공지사항 목록화면 출력 (필수공지사항 -> 일반공지사항 순으로 역순 정렬 + 검색기능에 따른 데이터 개수에 따라 페이지 재 정렬)
	public Page<InoticeDto> list(int page,int size,String search,String deptno) {
			
		 Pageable pageable = PageRequest.of(page,size);
		 
		 // 모든부서이거나, 부서 필터가 없을 때
		 if(deptno == null || deptno.isEmpty() || deptno.equals("")) {
			  return inoticeRepository.findByIntcttContaining(search,pageable);
		 }
		 
		 // 특정부서일 때 
		 else return inoticeRepository.findByDeptnoContaining(deptno,pageable);
	}
	
	// 전체공지사항 목록화면 총데이터 개수
	public long count() {
		return inoticeRepository.count();
	}
	
	// 전체공지사항 상세보기
	public InoticeDto detail(int intcno) {
		return inoticeRepository.findById(intcno).orElse(null);
	}
	
	// 전체공지사항 조회수증가
	public InoticeDto saveHits(int intcno) {
		
		InoticeDto dto = detail(intcno);
		int      hits = dto.getIntcht();
		hits++;
		dto.setIntcht(hits++);
		//  dto :: save시 키값이 포함 되어있는 경우 UPDATE 처리함
		return inoticeRepository.save(dto);
	}
	
	// 전체공지사항 유형(필수)값 개수
	public long countByIntcca() {
		return inoticeRepository.countByIntcca();
	}
	
	// 전체공지사항 등록,수정,삭제 서비스
	public InoticeDto notice(InoticeDto dto1) {
		
		// 수정하기 상황(바뀐내용 덮어쓰기)
		if(dto1.getIntcno() > 0 && dto1.getIntctt() != null && !dto1.getIntctt().equals("")) {
			
			InoticeDto dto2 = detail(dto1.getIntcno());
			
			// 조회수개수 초기화방지 그대로 유지
			dto1.setIntcht(dto2.getIntcht());
			
			// 등록일 초기화방지 그대로 유지
			dto1.setIntcrd(dto2.getIntcrd());
		}
		
		// 삭제하기 상황
		if(dto1.getIntcno() > 0 && dto1.getIntctt().equals("")) {
			inoticeRepository.deleteById(dto1.getIntcno());
		}
		
		// 등록하기 상황
		else {
			inoticeRepository.save(dto1);
		}
		
		return dto1;
	}
}