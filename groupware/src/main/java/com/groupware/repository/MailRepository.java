package com.groupware.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.groupware.dto.MailListDto;
import com.groupware.entity.MailDto;

import jakarta.transaction.Transactional;



public interface MailRepository extends JpaRepository<MailDto, Long>{
	
	
		// 전체 메일 리스트 조회 (삭제, 차단 안 된 내 메일 + 부서 대표메일 포함)
		@Query("SELECT m FROM MailDto m " +
		       "WHERE (" +
		       "  (m.senderId = :userId AND m.mailDeleteSender = 'N' AND m.mailFullyDeletedSender = 'N') " +
		       "  OR " +
		       "  (m.receiverId = :userId AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N' " +
		       "   AND m.senderId NOT IN (SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :userId)) " +
		       "  OR " +
		       "  (m.receiverId IN :deptEmails AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N')" +
		       ") " +
		       "AND m.mailDraft = 'N' " +
		       "AND NOT (m.senderId = :userId AND m.receiverId = :userId) " +
		       "ORDER BY m.sentAt DESC")
		Page<MailDto> findByUserInvolvedWithNoDeleteNoBlockWithDept(@Param("userId") String userId,
		                                                           @Param("deptEmails") List<String> deptEmails,
		                                                           Pageable pageable);
	
		// 전체메일 - 제목 검색
		@Query("SELECT m FROM MailDto m " +
		       "WHERE (" +
		       "  (m.senderId = :userId AND m.mailDeleteSender = 'N' AND m.mailFullyDeletedSender = 'N') " +
		       "  OR " +
		       "  (m.receiverId = :userId AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N' " +
		       "   AND m.senderId NOT IN (SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :userId)) " +
		       "  OR " +
		       "  (m.receiverId IN :deptEmails AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N')" +
		       ") " +
		       "AND m.mailDraft = 'N' " +
		       "AND m.subject LIKE %:keyword% " +
		       "AND NOT (m.senderId = :userId AND m.receiverId = :userId) " +
		       "ORDER BY m.sentAt DESC")
		Page<MailDto> findByUserAndSubjectContainingWithDept(@Param("userId") String userId,
		                                                    @Param("deptEmails") List<String> deptEmails,
		                                                    @Param("keyword") String keyword,
		                                                    Pageable pageable);
	
		// 전체메일 - 발신자명 검색
		@Query("SELECT m FROM MailDto m LEFT JOIN EmpDto e ON m.senderId = e.userid " +
		       "WHERE (" +
		       "  (m.senderId = :userId AND m.mailDeleteSender = 'N' AND m.mailFullyDeletedSender = 'N') " +
		       "  OR " +
		       "  (m.receiverId = :userId AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N' " +
		       "   AND m.senderId NOT IN (SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :userId)) " +
		       "  OR " +
		       "  (m.receiverId IN :deptEmails AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N')" +
		       ") " +
		       "AND m.mailDraft = 'N' " +
		       "AND LOWER(COALESCE(e.name, m.senderId)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		       "AND NOT (m.senderId = :userId AND m.receiverId = :userId) " +
		       "ORDER BY m.sentAt DESC")
		Page<MailDto> searchBySenderNameWithDept(@Param("userId") String userId,
		                                        @Param("deptEmails") List<String> deptEmails,
		                                        @Param("keyword") String keyword,
		                                        Pageable pageable);
	
		// 전체메일 - 수신자명 검색
		@Query("SELECT m FROM MailDto m LEFT JOIN EmpDto e ON m.receiverId = e.userid " +
		       "WHERE (" +
		       "  (m.senderId = :userId AND m.mailDeleteSender = 'N' AND m.mailFullyDeletedSender = 'N') " +
		       "  OR " +
		       "  (m.receiverId = :userId AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N' " +
		       "   AND m.senderId NOT IN (SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :userId)) " +
		       "  OR " +
		       "  (m.receiverId IN :deptEmails AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N')" +
		       ") " +
		       "AND m.mailDraft = 'N' " +
		       "AND LOWER(COALESCE(e.name, m.receiverId)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		       "AND NOT (m.senderId = :userId AND m.receiverId = :userId) " +
		       "ORDER BY m.sentAt DESC")
		Page<MailDto> searchByReceiverNameWithDept(@Param("userId") String userId,
		                                          @Param("deptEmails") List<String> deptEmails,
		                                          @Param("keyword") String keyword,
		                                          Pageable pageable);
	
		// 전체메일 - 날짜 검색
		@Query("SELECT m FROM MailDto m " +
		       "WHERE (" +
		       "  (m.senderId = :userId AND m.mailDeleteSender = 'N' AND m.mailFullyDeletedSender = 'N') " +
		       "  OR " +
		       "  (m.receiverId = :userId AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N' " +
		       "   AND m.senderId NOT IN (SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :userId)) " +
		       "  OR " +
		       "  (m.receiverId IN :deptEmails AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N')" +
		       ") " +
		       "AND m.mailDraft = 'N' " +
		       "AND m.sentAt BETWEEN :start AND :end " +
		       "AND NOT (m.senderId = :userId AND m.receiverId = :userId) " +
		       "ORDER BY m.sentAt DESC")
		Page<MailDto> findByUserAndSentAtBetweenWithDept(@Param("userId") String userId,
		                                                @Param("deptEmails") List<String> deptEmails,
		                                                @Param("start") LocalDateTime start,
		                                                @Param("end") LocalDateTime end,
		                                                Pageable pageable);
	
		// 전체메일 기본 검색 (내용 기준)
		@Query("SELECT m FROM MailDto m " +
		       "WHERE (" +
		       "  (m.senderId = :userId AND m.mailDeleteSender = 'N' AND m.mailFullyDeletedSender = 'N') " +
		       "  OR " +
		       "  (m.receiverId = :userId AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N' " +
		       "   AND m.senderId NOT IN (SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :userId)) " +
		       "  OR " +
		       "  (m.receiverId IN :deptEmails AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N')" +
		       ") " +
		       "AND m.mailDraft = 'N' " +
		       "AND m.content LIKE %:keyword% " +
		       "AND NOT (m.senderId = :userId AND m.receiverId = :userId) " +
		       "ORDER BY m.sentAt DESC")
		Page<MailDto> findByUserAndContentContainingWithDept(@Param("userId") String userId,
		                                                    @Param("deptEmails") List<String> deptEmails,
		                                                    @Param("keyword") String keyword,
		                                                    Pageable pageable);


    	// 사용자가 발신자 휴지통이동
    	@Modifying
	    @Transactional
	    @Query("update MailDto m set m.mailDeleteSender = 'Y' where m.mailno = :mailno and m.senderId = :userId")
	    int softDeleteBySender(@Param("mailno") Long mailno, @Param("userId") String userId);
	
    	// 사용자가 수신자 휴지통이동
	    @Modifying
	    @Transactional
	    @Query("update MailDto m set m.mailDeleteReceiver = 'Y' where m.mailno = :mailno and m.receiverId = :userId")
	    int softDeleteByReceiver(@Param("mailno") Long mailno, @Param("userId") String userId);
	
	    
	    
	    // 메일 전체삭제시 전체메일 조회
	    @Query("""
	        SELECT m.mailno FROM MailDto m 
	        WHERE (m.senderId = :userId AND m.mailDeleteSender = 'N' AND m.mailFullyDeletedSender = 'N')
	           OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N')
	    """)
	    List<Long> findAllIdsByUser(@Param("userId") String userId);

	    
	    // 메일 전체삭제 (휴지통 이동) 
	    @Modifying
	    @Transactional
	    @Query("""
	        UPDATE MailDto m
	        SET 
	            m.mailDeleteSender = CASE WHEN m.senderId = :userId THEN 'Y' ELSE m.mailDeleteSender END,
	            m.mailDeleteReceiver = CASE WHEN m.receiverId = :userId THEN 'Y' ELSE m.mailDeleteReceiver END
	        WHERE (m.senderId = :userId AND m.mailDeleteSender = 'N')
	           OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'N')
	    """)
	    int softDeleteAllByUser(@Param("userId") String userId);

	 // 수신메일함 (전체 조회, 내가 나한테 보낸 메일 포함 가능)
	    List<MailDto> findByReceiverIdAndMailDeleteReceiver(String receiverId, String mailDeleteReceiver);

	    // 수신메일함 - 내게 보낸 메일 제외 (기본 목록 조회용, 페이징 처리) + 차단된 발신자 제외
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.receiverId = :receiverId " +
	           "AND m.mailDeleteReceiver = :mailDeleteReceiver " +
	           "AND m.senderId <> :receiverId " +
	           "AND m.senderId NOT IN (SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :receiverId) " +
	           "ORDER BY m.sentAt DESC")
	    Page<MailDto> findByReceiverIdAndMailDeleteReceiverExcludeSelf(@Param("receiverId") String receiverId, 
	                                                                   @Param("mailDeleteReceiver") String mailDeleteReceiver, 
	                                                                   Pageable pageable);

	    // 수신메일함 - 발신자 이름 검색 (EmpDto 조인, 내가 나한테 쓴 메일 제외) + 차단된 발신자 제외
	    @Query("SELECT m FROM MailDto m LEFT JOIN EmpDto e ON m.senderId = e.userid " +
	           "WHERE m.receiverId = :userId " +
	           "AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N' " +
	           "AND LOWER(COALESCE(e.name, m.senderId)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	           "AND m.senderId <> :userId " +
	           "AND m.senderId NOT IN (SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :userId) " +
	           "ORDER BY m.sentAt DESC")
	    Page<MailDto> searchReceivedBySenderNameExcludeSelf(@Param("userId") String userId,
	                                                       @Param("keyword") String keyword,
	                                                       Pageable pageable);

	    // 수신메일함 - 제목 검색 + 차단된 발신자 제외
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.receiverId = :userId " +
	           "AND m.mailDeleteReceiver = :mailDeleteReceiver " +
	           "AND m.senderId <> :userId " +
	           "AND m.subject LIKE %:keyword% " +
	           "AND m.senderId NOT IN (SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :userId) " +
	           "ORDER BY m.sentAt DESC")
	    Page<MailDto> findByReceiverIdAndSubjectContainingAndMailDeleteReceiverExcludeSelf(@Param("userId") String userId,
	                                                                                      @Param("keyword") String keyword,
	                                                                                      @Param("mailDeleteReceiver") String mailDeleteReceiver,
	                                                                                      Pageable pageable);

	    // 수신메일함 - 내용 검색 + 차단된 발신자 제외
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.receiverId = :userId " +
	           "AND m.mailDeleteReceiver = :mailDeleteReceiver " +
	           "AND m.senderId <> :userId " +
	           "AND m.content LIKE %:keyword% " +
	           "AND m.senderId NOT IN (SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :userId) " +
	           "ORDER BY m.sentAt DESC")
	    Page<MailDto> findByReceiverIdAndContentContainingAndMailDeleteReceiverExcludeSelf(@Param("userId") String userId,
	                                                                                       @Param("keyword") String keyword,
	                                                                                       @Param("mailDeleteReceiver") String mailDeleteReceiver,
	                                                                                       Pageable pageable);

	    // 수신메일함 - 날짜 검색 + 차단된 발신자 제외
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.receiverId = :userId " +
	           "AND m.mailDeleteReceiver = :mailDeleteReceiver " +
	           "AND m.senderId <> :userId " +
	           "AND m.sentAt BETWEEN :start AND :end " +
	           "AND m.senderId NOT IN (SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :userId) " +
	           "ORDER BY m.sentAt DESC")
	    Page<MailDto> findByReceiverIdAndSentAtBetweenAndMailDeleteReceiverExcludeSelf(
	        @Param("userId") String userId,
	        @Param("start") LocalDateTime start,
	        @Param("end") LocalDateTime end,
	        @Param("mailDeleteReceiver") String mailDeleteReceiver,
	        Pageable pageable);
	 // 발신메일함 - 삭제 안 된 메일 목록 (임시저장 제외)
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.senderId = :senderId " +
	           "AND m.mailDeleteSender = :mailDeleteSender " +
	           "AND m.mailDraft = 'N' " +
	           "ORDER BY m.sentAt DESC")
	    Page<MailDto> findBySenderIdAndMailDeleteSender(
	        @Param("senderId") String senderId,
	        @Param("mailDeleteSender") String mailDeleteSender,
	        Pageable pageable);

	    // 발신메일함 - 삭제 안 된 메일 목록 (내가 나한테 쓴 메일 제외, 임시저장 제외)
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.senderId = :senderId " +
	           "AND m.mailDeleteSender = :mailDeleteSender " +
	           "AND m.receiverId <> :senderId " +
	           "AND m.mailDraft = 'N' " +
	           "ORDER BY m.sentAt DESC")
	    Page<MailDto> findBySenderIdAndMailDeleteSenderExcludeSelf(
	        @Param("senderId") String senderId,
	        @Param("mailDeleteSender") String mailDeleteSender,
	        Pageable pageable);

	    // 발신메일함 - 수신자 ID로 검색 (부분 일치, 임시저장 제외)
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.senderId = :senderId " +
	           "AND m.receiverId LIKE CONCAT('%', :receiverId, '%') " +
	           "AND m.mailDeleteSender = :mailDeleteSender " +
	           "AND m.mailDraft = 'N'")
	    Page<MailDto> findBySenderIdAndReceiverIdContainingAndMailDeleteSender(
	        @Param("senderId") String senderId,
	        @Param("receiverId") String receiverId,
	        @Param("mailDeleteSender") String mailDeleteSender,
	        Pageable pageable);

	    // 발신메일함 - 제목 검색 (임시저장 제외)
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.senderId = :senderId " +
	           "AND m.subject LIKE CONCAT('%', :subject, '%') " +
	           "AND m.mailDeleteSender = :mailDeleteSender " +
	           "AND m.mailDraft = 'N'")
	    Page<MailDto> findBySenderIdAndSubjectContainingAndMailDeleteSender(
	        @Param("senderId") String senderId,
	        @Param("subject") String subject,
	        @Param("mailDeleteSender") String mailDeleteSender,
	        Pageable pageable);

	    // 발신메일함 - 내용 검색 (임시저장 제외)
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.senderId = :senderId " +
	           "AND m.content LIKE CONCAT('%', :content, '%') " +
	           "AND m.mailDeleteSender = :mailDeleteSender " +
	           "AND m.mailDraft = 'N'")
	    Page<MailDto> findBySenderIdAndContentContainingAndMailDeleteSender(
	        @Param("senderId") String senderId,
	        @Param("content") String content,
	        @Param("mailDeleteSender") String mailDeleteSender,
	        Pageable pageable);

	    // 발신메일함 - 날짜 검색 (날짜 범위, 임시저장 제외)
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.senderId = :senderId " +
	           "AND m.sentAt BETWEEN :start AND :end " +
	           "AND m.mailDeleteSender = :mailDeleteSender " +
	           "AND m.mailDraft = 'N'")
	    Page<MailDto> findBySenderIdAndSentAtBetweenAndMailDeleteSender(
	        @Param("senderId") String senderId,
	        @Param("start") LocalDateTime start,
	        @Param("end") LocalDateTime end,
	        @Param("mailDeleteSender") String mailDeleteSender,
	        Pageable pageable);

	    // 발신메일함 - 수신자 이름 기준 검색 (EmpDto 조인, 이름 또는 아이디 포함 검색, 임시저장 제외)
	    @Query("SELECT m FROM MailDto m LEFT JOIN EmpDto e ON m.receiverId = e.userid " +
	           "WHERE m.senderId = :userId " +
	           "AND (LOWER(COALESCE(e.name, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.receiverId) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
	           "AND m.mailDeleteSender = 'N' " +
	           "AND m.mailDraft = 'N'")
	    Page<MailDto> searchSendByReceiverName(
	        @Param("userId") String userId,
	        @Param("keyword") String keyword,
	        Pageable pageable);

	    // 전체 발신메일 목록 (삭제 안 된 것만, 임시저장 제외)
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.senderId = :senderId " +
	           "AND m.mailDeleteSender = :mailDeleteSender " +
	           "AND m.mailDraft = 'N'")
	    List<MailDto> findBySenderIdAndMailDeleteSender(
	        @Param("senderId") String senderId,
	        @Param("mailDeleteSender") String mailDeleteSender);

	 // 휴지통 - 삭제된 메일 전체 (내가 보낸 or 받은 것들, 완전삭제 안 된 것만)
	    @Query("SELECT m FROM MailDto m WHERE " +
	           "((m.senderId = :userId AND m.mailDeleteSender = 'Y' AND m.mailFullyDeletedSender = 'N') " +
	           "OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'Y' AND m.mailFullyDeletedReceiver = 'N'))")
	    Page<MailDto> findTrashMails(@Param("userId") String userId, Pageable pageable);

	    // 휴지통 - 제목 검색 (완전삭제 안 된 것만)
	    @Query("SELECT m FROM MailDto m WHERE " +
	           "((m.senderId = :userId AND m.mailDeleteSender = 'Y' AND m.mailFullyDeletedSender = 'N') " +
	           "OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'Y' AND m.mailFullyDeletedReceiver = 'N')) " +
	           "AND m.subject LIKE %:keyword%")
	    Page<MailDto> searchTrashBySubject(@Param("userId") String userId, @Param("keyword") String keyword, Pageable pageable);

	    // 휴지통 - 내용 검색 (완전삭제 안 된 것만)
	    @Query("SELECT m FROM MailDto m WHERE " +
	           "((m.senderId = :userId AND m.mailDeleteSender = 'Y' AND m.mailFullyDeletedSender = 'N') " +
	           "OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'Y' AND m.mailFullyDeletedReceiver = 'N')) " +
	           "AND m.content LIKE %:keyword%")
	    Page<MailDto> searchTrashByContent(@Param("userId") String userId, @Param("keyword") String keyword, Pageable pageable);

	    // 휴지통 - 날짜 검색 (완전삭제 안 된 것만)
	    @Query("SELECT m FROM MailDto m WHERE " +
	           "((m.senderId = :userId AND m.mailDeleteSender = 'Y' AND m.mailFullyDeletedSender = 'N') " +
	           "OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'Y' AND m.mailFullyDeletedReceiver = 'N')) " +
	           "AND m.sentAt BETWEEN :start AND :end")
	    Page<MailDto> searchTrashByDate(@Param("userId") String userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

	    // 휴지통 - 발신자 이름으로 검색 (EmpDto LEFT JOIN, 완전삭제 안 된 것만)
	    @Query("SELECT m FROM MailDto m LEFT JOIN EmpDto e ON m.senderId = e.userid " +
	           "WHERE ((m.senderId = :userId AND m.mailDeleteSender = 'Y' AND m.mailFullyDeletedSender = 'N') " +
	           "OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'Y' AND m.mailFullyDeletedReceiver = 'N')) " +
	           "AND (:keyword = '' OR " +
	           "LOWER(COALESCE(e.name, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	           "OR LOWER(m.senderId) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	    Page<MailDto> searchTrashBySenderName(@Param("userId") String userId, @Param("keyword") String keyword, Pageable pageable);

	    // 휴지통 - 수신자 이름으로 검색 (EmpDto LEFT JOIN, 완전삭제 안 된 것만)
	    @Query("SELECT m FROM MailDto m LEFT JOIN EmpDto e ON m.receiverId = e.userid " +
	           "WHERE ((m.senderId = :userId AND m.mailDeleteSender = 'Y' AND m.mailFullyDeletedSender = 'N') " +
	           "OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'Y' AND m.mailFullyDeletedReceiver = 'N')) " +
	           "AND (:keyword = '' OR " +
	           "LOWER(COALESCE(e.name, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	           "OR LOWER(m.receiverId) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	    Page<MailDto> searchTrashByReceiverName(@Param("userId") String userId, @Param("keyword") String keyword, Pageable pageable);


	    // 페이징 없는 전체 휴지통 메일 조회 (검색 없음, 완전삭제 여부는 상관없음)
	    @Query("SELECT m FROM MailDto m WHERE (m.senderId = :userId AND m.mailDeleteSender = 'Y') OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'Y')")
	    List<MailDto> findTrashMailsAll(@Param("userId") String userId);

	    // 페이징 없는 제목 검색 (완전삭제 여부는 상관없음)
	    @Query("SELECT m FROM MailDto m WHERE " +
	           "((m.senderId = :userId AND m.mailDeleteSender = 'Y') " +
	           "OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'Y')) " +
	           "AND m.subject LIKE %:keyword%")
	    List<MailDto> searchTrashBySubjectAll(@Param("userId") String userId, @Param("keyword") String keyword);

	    // 페이징 없는 내용 검색 (완전삭제 여부는 상관없음)
	    @Query("SELECT m FROM MailDto m WHERE " +
	           "((m.senderId = :userId AND m.mailDeleteSender = 'Y') " +
	           "OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'Y')) " +
	           "AND m.content LIKE %:keyword%")
	    List<MailDto> searchTrashByContentAll(@Param("userId") String userId, @Param("keyword") String keyword);

	    // 페이징 없는 날짜 검색 (완전삭제 여부는 상관없음)
	    @Query("SELECT m FROM MailDto m WHERE " +
	           "((m.senderId = :userId AND m.mailDeleteSender = 'Y') " +
	           "OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'Y')) " +
	           "AND m.sentAt BETWEEN :start AND :end")
	    List<MailDto> searchTrashByDateAll(@Param("userId") String userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	    // 페이징 없는 발신자 이름 검색 (EmpDto 조인, 완전삭제 여부는 상관없음)
	    @Query("SELECT m FROM MailDto m JOIN EmpDto e ON m.senderId = e.userid " +
	           "WHERE ((m.senderId = :userId AND m.mailDeleteSender = 'Y') " +
	           "OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'Y')) " +
	           "AND e.name LIKE %:keyword%")
	    List<MailDto> searchTrashBySenderNameAll(@Param("userId") String userId, @Param("keyword") String keyword);

	    // 페이징 없는 수신자 이름 검색 (EmpDto 조인, 완전삭제 여부는 상관없음)
	    @Query("SELECT m FROM MailDto m JOIN EmpDto e ON m.receiverId = e.userid " +
	           "WHERE ((m.senderId = :userId AND m.mailDeleteSender = 'Y') " +
	           "OR (m.receiverId = :userId AND m.mailDeleteReceiver = 'Y')) " +
	           "AND e.name LIKE %:keyword%")
	    List<MailDto> searchTrashByReceiverNameAll(@Param("userId") String userId, @Param("keyword") String keyword);

	    // 내게 쓴 메일함 - 기본 목록 조회
	   
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.senderId = :userId " +
	           "AND m.receiverId = :userId " +
	           "AND m.mailDeleteSender = 'N' " +
	           "ORDER BY m.sentAt DESC")
	    Page<MailDto> findMailToMeByUserId(@Param("userId") String userId, Pageable pageable);

	    // 내게 쓴 메일함 - 제목 검색
	   
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.senderId = :userId " +
	           "AND m.receiverId = :userId " +
	           "AND m.mailDeleteSender = 'N' " +
	           "AND LOWER(m.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	           "ORDER BY m.sentAt DESC")
	    Page<MailDto> searchMailToMeBySubject(@Param("userId") String userId, 
	                                          @Param("keyword") String keyword, 
	                                          Pageable pageable);

	    // 내게 쓴 메일함 - 내용 검색
	   
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.senderId = :userId " +
	           "AND m.receiverId = :userId " +
	           "AND m.mailDeleteSender = 'N' " +
	           "AND LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	           "ORDER BY m.sentAt DESC")
	    Page<MailDto> searchMailToMeByContent(@Param("userId") String userId, 
	                                          @Param("keyword") String keyword, 
	                                          Pageable pageable);

	    // 내게 쓴 메일함 - 날짜 검색
	     
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.senderId = :userId " +
	           "AND m.receiverId = :userId " +
	           "AND m.mailDeleteSender = 'N' " +
	           "AND m.sentAt >= :start AND m.sentAt < :end " +
	           "ORDER BY m.sentAt DESC")
	    Page<MailDto> searchMailToMeByDate(@Param("userId") String userId,
	                                       @Param("start") LocalDateTime start,
	                                       @Param("end") LocalDateTime end,
	                                       Pageable pageable);

	    // 내게 쓴 메일함 - 기본 검색 (제목 또는 내용에 키워드 포함)
	    
	    @Query("SELECT m FROM MailDto m " +
	           "WHERE m.senderId = :userId " +
	           "AND m.receiverId = :userId " +
	           "AND m.mailDeleteSender = :mailDeleteSender " +
	           "AND (LOWER(m.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	           "     OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
	           "ORDER BY m.sentAt DESC")
	    
	    
	    Page<MailDto> findBySenderIdAndReceiverIdAndMailDeleteSenderAnd(
	        @Param("userId") String senderId,
	        @Param("userId") String receiverId,
	        @Param("mailDeleteSender") String mailDeleteSender,
	        @Param("keyword") String keyword,
	        Pageable pageable);
	    
	    // 내게쓴메일함 전체삭제 
	    @Modifying
	    @Query("UPDATE MailDto m SET m.mailDeleteSender = 'Y' " +
	           "WHERE m.senderId = :userId AND m.receiverId = :userId AND m.mailDeleteSender = 'N'")
	    void markDeletedMailToMe(@Param("userId") String userId);

	    // 임시보관함 - 제목 검색
	    Page<MailDto> findBySenderIdAndMailDraftAndSubjectContaining(String senderId, String mailDraft, String subject, Pageable pageable);

	    // 임시보관함 - 내용 검색
	    Page<MailDto> findBySenderIdAndMailDraftAndContentContaining(String senderId, String mailDraft, String content, Pageable pageable);

	    // 임시보관함 - 날짜 검색 (문자열로 처리 시)
	    Page<MailDto> findBySenderIdAndMailDraftAndSentAtContaining(String senderId, String mailDraft, String sentAt, Pageable pageable);

	    // 임시보관함 - 수신자 검색 (사실 senderId와 동일하므로 필요 없을 수도 있음)
	    Page<MailDto> findBySenderIdAndMailDraftAndReceiverIdContaining(String senderId, String mailDraft, String receiverId, Pageable pageable);

	    // 임시보관함 - 기본 통합 검색 (제목 or 내용)
	    @Query("SELECT m FROM MailDto m WHERE m.senderId = :senderId AND m.mailDraft = 'Y' AND (m.subject LIKE %:keyword% OR m.content LIKE %:keyword%)")
	    Page<MailDto> searchDraftMail(@Param("senderId") String senderId, @Param("keyword") String keyword, Pageable pageable);

	    // 임시보관함 메일 전체 조회 (리스트 반환)
	    List<MailDto> findBySenderIdAndMailDraft(String senderId, String mailDraft);
	    
	    // 임시보관함 메일 전체삭제 
	    @Modifying
	    @Query("UPDATE MailDto m SET m.mailDeleteSender = 'Y' WHERE m.senderId = :userId AND m.mailDraft = 'Y' AND m.mailDeleteSender = 'N'")
	    void markAllDraftsDeletedBySender(@Param("userId") String userId);
	    
	    // 메일 임시보관함 리스트
	    @Query("SELECT m FROM MailDto m " +
	    	       "WHERE m.senderId = :userId " +
	    	       "AND m.mailDraft = 'Y' " +
	    	       "AND m.mailDeleteSender = 'N' " +
	    	       "ORDER BY m.sentAt DESC")
	    	Page<MailDto> findDraftMailsBySenderIdAndNotDeleted(@Param("userId") String userId, Pageable pageable);



	  //임시보관함 메일쓰기
	     MailDto findByMailnoAndSenderIdAndMailDraft(Long mailno, String senderId, String mailDraft);
	    
	 // 차단된 사용자로부터 온 메일만 조회 (내가 나한테 보낸 메일 제외)
	    @Query("""
	        SELECT m FROM MailDto m
	        WHERE m.receiverId = :userId
	          AND m.senderId IN (SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :userId)
	          AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N'
	          AND NOT (m.senderId = :userId AND m.receiverId = :userId)
	        ORDER BY m.sentAt DESC
	    """)
	    Page<MailDto> findBlockedMails(@Param("userId") String userId, Pageable pageable);

	    // 키워드로 통합검색 (제목, 내용, 발신자명) + 차단자 필터링 (내가 나한테 쓴 메일 제외)
	    @Query("""
	        SELECT m FROM MailDto m LEFT JOIN EmpDto sender ON m.senderId = sender.userid
	        WHERE m.receiverId = :userId
	          AND m.senderId IN (SELECT b.blockedId FROM BlockDto b WHERE b.blockerId = :userId)
	          AND m.mailDeleteReceiver = 'N' AND m.mailFullyDeletedReceiver = 'N'
	          AND (
	            LOWER(COALESCE(sender.name, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
	            OR LOWER(m.subject) LIKE LOWER(CONCAT('%', :keyword, '%'))
	            OR LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
	          )
	          AND NOT (m.senderId = :userId AND m.receiverId = :userId)
	        ORDER BY m.sentAt DESC
	    """)
	    Page<MailDto> searchBlockedMails(@Param("userId") String userId,
	                                    @Param("keyword") String keyword,
	                                    Pageable pageable);
	    
	 // 수신 메일 중 차단된 사용자 메일 휴지통 이동
	    @Modifying
	    @Query("UPDATE MailDto m SET m.mailDeleteReceiver = 'Y' " +
	           "WHERE m.receiverId = :userId " +
	           "AND m.senderId IN :blockedIds " +
	           "AND m.mailDeleteReceiver = 'N' " +
	           "AND m.mailFullyDeletedReceiver = 'N'")
	    int updateMailDeleteReceiverToY(@Param("userId") String userId, @Param("blockedIds") List<String> blockedIds);

	 // (필요 시) 발신 메일도 휴지통 이동 처리
	    @Modifying
	    @Query("UPDATE MailDto m SET m.mailDeleteSender = 'Y' " +
	           "WHERE m.senderId = :userId " +
	           "AND m.receiverId IN :blockedIds " +
	           "AND m.mailDeleteSender = 'N' " +
	           "AND m.mailFullyDeletedSender = 'N'")
	    int updateMailDeleteSenderToY(@Param("userId") String userId, @Param("blockedIds") List<String> blockedIds);

	    // mailno(메일번호) + senderId(보낸사람)로 임시저장 메일 조회 ( 포함)
	    Optional<MailDto> findByMailnoAndSenderId(Long mailno, String senderId);

	
	    



}


