package com.groupware.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.groupware.entity.TeamMailDto;

public interface TeamMailRepository extends JpaRepository<TeamMailDto, String> {

    Optional<TeamMailDto> findByTeamName(String teamName);  // 부서명으로 대표메일 조회
    
    @Query("SELECT t.teamMail FROM TeamMailDto t WHERE t.teamName = :teamName")
    String findTeamMailByTeamName(@Param("teamName") String teamName); // 부서명으로 부서전체정보 조회 
    
    
    Optional<TeamMailDto> findByTeamMail(String teamMail);// 대표주소로 부서 전체정보 조희
    
    
    // 부서 대표메일 리스트 조회 (userId가 속한 부서 기준)
    @Query("SELECT DISTINCT t.teamMail FROM TeamMailDto t " +
    	       "JOIN EmpDto e ON e.dept = t.teamId " +
    	       "WHERE e.userid = :userId")
    	List<String> findTeamMailsByUserId(@Param("userId") String userId);

}


