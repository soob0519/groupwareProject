package com.groupware.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.groupware.entity.GnoticeDto;
import com.groupware.repository.GnoticeRepository;

@Service
public class GnoticeService {

	// 리퍼지토리 설정
	public final GnoticeRepository gnoticeRepository;
	
	// 전체컨트롤러 서비스 실행 설정
	public GnoticeService(GnoticeRepository gnoticeRepository) {
		this.gnoticeRepository = gnoticeRepository;
	}
	
	// 일반공지사항 목록화면 출력 (검색기능에 따른 데이터 개수에 따라 페이지 재 정렬)
	public Page<GnoticeDto> list(int page,int size,String search,String deptno) {
			
		 Pageable pageable = PageRequest.of(page,size);
		 
		// 모든부서이거나, 부서 필터가 없을 때
		 if(deptno == null || deptno.isEmpty() || deptno.equals("")) {
			  return gnoticeRepository.findByGntcttContaining(search,pageable);
		 }
		 
		 // 특정부서일 때 
		 else return gnoticeRepository.findByDeptnoContaining(deptno,pageable);
	}
	
	// 일반공지사항 목록화면 총데이터 개수
	public Long count() {
		return gnoticeRepository.count();
	}
	
	// 일반공지사항 상세보기
	public GnoticeDto detail(int gntcno) {
		return gnoticeRepository.findById(gntcno).orElse(null);
	}
	
	// 일반공지사항 조회수증가
	public GnoticeDto saveHits(int gntcno) {
		
		GnoticeDto dto = detail(gntcno);
		int      hits = dto.getGntcht();
		hits++;
		dto.setGntcht(hits++);
		//  dto :: save시 키값이 포함 되어있는 경우 UPDATE 처리함
		return gnoticeRepository.save(dto);
	}
	
	// 일반공지사항 등록,수정,삭제 서비스
	public GnoticeDto notice(GnoticeDto dto1) {
		
		// 수정하기 상황(바뀐내용 덮어쓰기)
		if(dto1.getGntcno() > 0 && dto1.getGntctt() != null && !dto1.getGntctt().equals("")) {
			
			GnoticeDto dto2 = detail(dto1.getGntcno());
			
			// 조회수개수 초기화방지 그대로 유지
			dto1.setGntcht(dto2.getGntcht());
			
			// 등록일 초기화방지 그대로 유지
			dto1.setGntcrd(dto2.getGntcrd());
		}
		
		// 삭제하기 상황
		if(dto1.getGntcno() > 0 && dto1.getGntctt().equals("")) {
			gnoticeRepository.deleteById(dto1.getGntcno());
		}
		
		// 등록하기 상황
		else {
			gnoticeRepository.save(dto1);
		}
		
		return dto1;
	}
}