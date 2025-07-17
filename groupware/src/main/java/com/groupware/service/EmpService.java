package com.groupware.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.groupware.entity.EmpDto;
import com.groupware.repository.CodeRepository;
import com.groupware.repository.EmpRepository;


@Service
public class EmpService {
	
	public final EmpRepository empRepository;
	public final CodeRepository codeRepository;
	public EmpService(EmpRepository empRepository,
						CodeRepository codeRepository) {
		this.empRepository = empRepository;
		this.codeRepository = codeRepository;
		
	}
	
	/**
	 *   등록 처리1
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
	 *  목록 출력 이름검색
	 */
	public Page<EmpDto> searchByName(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("empno").descending());
		return empRepository.findByNameContainingIgnoreCase(keyword, pageable);
	}
	
	/**
	 *  목록 출력 부서검색
	 */
	public Page<EmpDto> searchByDept(String deptname, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("empno").descending());
		
		// 부서 한글 이름으로 코드값 가져오기
		String deptCode = codeRepository.findUcodeByNcode(deptname);
		
		// 코드값이 없으면 빈 결과 반환
		if (deptCode == null) {
			return Page.empty(pageable);
		}
		return empRepository.findByDept(deptCode, pageable);
	}
	
	// 상태만 필터링하여 리스트 조회
	public Page<EmpDto> listByState(String state, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size, Sort.by("empno").descending());
	    return empRepository.findByState(state, pageable);
	}

	// 이름 + 상태 검색
	public Page<EmpDto> searchByNameAndState(String keyword, String state, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size, Sort.by("empno").descending());
	    return empRepository.findByNameContainingIgnoreCaseAndState(keyword, state, pageable);
	}

	// 부서 + 상태 검색
	public Page<EmpDto> searchByDeptAndState(String deptname, String state, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size, Sort.by("empno").descending());

	    String deptCode = codeRepository.findUcodeByNcode(deptname);
	    if (deptCode == null) {
	        return Page.empty(pageable);
	    }
	    return empRepository.findByDeptAndState(deptCode, state, pageable);
	}
	
	/**
	 *  상세정보
	 */
	public EmpDto detail(int empno) {
		return empRepository.findById(empno).orElse(null);
	}
	
	/**
	 * 상세보기
	 */
	public EmpDto getFindById(Integer empno) {
		return empRepository.findById(empno).orElse(null);
	}
	
	/**
	 * 사원 상태 변경 (재직중 <-> 퇴사자)
	 */
	public void toggleState(int empno) {
	    EmpDto emp = empRepository.findById(empno).orElse(null);
		if (emp != null) {
			String currentState = emp.getState();
			if ("Y".equals(currentState)) {
				// 재직중 → 퇴직 처리
				emp.setState("N");
				emp.setQdate(new Timestamp(System.currentTimeMillis()));  // 오늘 날짜 퇴사일로 설정
			} else {
				// 퇴직 → 재직중 처리
			emp.setState("Y");
			emp.setQdate(null);  // 퇴사일 초기화
				}
				empRepository.save(emp);
		}
	}

	public List<EmpDto> findAll() {
		return empRepository.findAll();
	}

	public EmpDto findById(int empno) {
		return empRepository.findById(empno).orElse(null);
	}


	
}
