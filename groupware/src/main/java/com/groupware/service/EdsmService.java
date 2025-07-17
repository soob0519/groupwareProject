package com.groupware.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.groupware.entity.EdsmDto;
import com.groupware.repository.EdsmRepository;

@Service
public class EdsmService {
	
	public final EdsmRepository edsmRepository;
	public EdsmService(EdsmRepository edsmRepository) {
		this.edsmRepository = edsmRepository;
	}
	
	public EdsmDto save(EdsmDto dto) {
		
		return edsmRepository.save(dto);
	}
	
	// 임시보관함 목록1
	public List<EdsmDto> findDraftsByEmpno(int empno) {
	    return edsmRepository.findDraftsByEmpno(empno);
	}
	
	public EdsmDto findById(int edsmno) {
		return edsmRepository.findById(edsmno).orElseThrow(() -> new RuntimeException("문서 없음"));
	}
	
	//결재목록 상태 검색 페이징
	public Page<EdsmDto> findDocsByEmpno(int empno,
								            String status,
								            String searchType,
								            String keyword,
								            Pageable pageable) {

        // 검색어버젼
        if (keyword != null && !keyword.isBlank()) {
            if ("drafter".equals(searchType)) {
                return edsmRepository.findDocsByEmpnoAndDrafter(empno, keyword, pageable);
            }
            // 제목 검색
            return edsmRepository.findDocsByEmpnoAndTitle(empno, keyword, pageable);
        }

        // 검색어 없는 버젼
        if (status == null || status.isEmpty()) {
            return edsmRepository.findDocsByEmpno(empno, pageable);
        }
        return edsmRepository.findDocsByEmpnoAndEdst(empno, status, pageable);
    }

	public void deleteDraft(int edsmno) {
        edsmRepository.deleteById(edsmno);
		
	}
	
	
}
