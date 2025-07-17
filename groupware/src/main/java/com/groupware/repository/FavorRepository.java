package com.groupware.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.groupware.entity.FavorDto;


public interface FavorRepository extends JpaRepository<FavorDto,Integer> {
	
	// 특정 사용자가 즐겨찾기에 추가한 사원 목록
    List<FavorDto> findByEmpno(int empno);

    // 이미 추가했는지 확인
    boolean existsByEmpnoAndFempno(int empno, int fempno);

    // 삭제
    void deleteByEmpnoAndFempno(int empno, int fempno);

    // 즐겨찾기 row 반환 (삭제할 때 씀)
    Optional<FavorDto> findByEmpnoAndFempno(Integer empno, Integer fempno);
}
