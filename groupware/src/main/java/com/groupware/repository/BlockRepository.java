package com.groupware.repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.groupware.entity.BlockDto;

import jakarta.transaction.Transactional;

public interface BlockRepository extends JpaRepository<BlockDto, Long> {
    

  
    List<BlockDto> findAllByBlockerId(String blockerId);
    Optional<BlockDto> findByBlockerIdAndBlockedId(String blockerId, String blockedId);
    Page<BlockDto> findByBlockerIdAndBlockedIdContaining(String blockerId, String blockedId, Pageable pageable);
	 // 전체 차단 해제 
	    @Modifying
	    @Transactional
	    void deleteAllByBlockerId(String blockerId);
	    
	    // blockerId가 차단한 blockedId 리스트만 조회하는 메서드
	    @Query("SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :blockerId")
	    List<String> findBlockedIdsByBlockerId(@Param("blockerId") String blockerId);
	    
	    // 로그인한 userId가 특정 사용자를 차단했는지 여부 확인
	    boolean existsByBlockerIdAndBlockedId(String blockerId, String blockedId);


}
