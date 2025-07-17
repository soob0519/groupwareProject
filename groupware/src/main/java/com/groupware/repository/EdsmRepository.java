package com.groupware.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.groupware.entity.EdsmDto;

public interface EdsmRepository extends JpaRepository<EdsmDto,Integer> {
		
		// 그냥 페이징1
		@Query("SELECT e FROM EdsmDto e "
				+ " WHERE e.isdraft = 'N' "
				+ "   AND (e.empno = :empno "
				+ "     OR e.edsmno IN ("
				+ "         SELECT l.edsmno FROM EdsmlineDto l WHERE l.empno = :empno"
				+ "       )"
				+ "    )"
				+ " ORDER BY e.wdate DESC")
		Page<EdsmDto> findDocsByEmpno(@Param("empno")int empno, Pageable pageable);

	  //상태별 페이징
		@Query("SELECT e FROM EdsmDto e "
				+ " WHERE e.isdraft = 'N' "
				+ "   AND (e.empno = :empno "
				+ "     OR e.edsmno IN ("
				+ "         SELECT l.edsmno FROM EdsmlineDto l WHERE l.empno = :empno"
				+ "       )"
				+ "    )"
				+ "   AND e.edst = :edst"
				+ " ORDER BY e.wdate DESC")
	  Page<EdsmDto> findDocsByEmpnoAndEdst(@Param("empno") int empno,
			  								@Param("edst") String edst,
			  								Pageable pageable);

		// 임시저장
		@Query("SELECT e FROM EdsmDto e WHERE e.isdraft = 'Y' AND e.empno = :empno")
		List<EdsmDto> findDraftsByEmpno(@Param("empno") int empno);
	  
		// 제목검색
		@Query("SELECT e FROM EdsmDto e "
				+ "	WHERE e.isdraft = 'N' AND (e.empno = :empno OR e.edsmno IN "
				+ "	( SELECT l.edsmno FROM EdsmlineDto l WHERE l.empno = :empno)) "
				+ "	AND LOWER(e.edtitle) LIKE CONCAT('%', LOWER(:keyword), '%')ORDER BY e.wdate DESC")
		Page<EdsmDto> findDocsByEmpnoAndTitle( @Param("empno")   int empno,
												@Param("keyword") String keyword,
												Pageable pageable);

		// 기안자 검색
		@Query("SELECT e FROM EdsmDto e "
				+ "	WHERE e.isdraft = 'N' AND (e.empno = :empno OR e.edsmno IN "
				+ "	( SELECT l.edsmno  FROM EdsmlineDto l  WHERE l.empno = :empno )  ) "
				+ "	AND e.empno IN "
				+ "	(  SELECT emp.empno  FROM EmpDto emp  "
				+ "	WHERE LOWER(emp.name) LIKE CONCAT('%', LOWER(:keyword), '%') ) "
				+ "	ORDER BY e.wdate DESC")
		Page<EdsmDto> findDocsByEmpnoAndDrafter( @Param("empno")   int empno,
		  										@Param("keyword") String keyword,
		  										Pageable pageable);

}
