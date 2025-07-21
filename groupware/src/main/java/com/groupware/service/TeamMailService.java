package com.groupware.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.groupware.repository.TeamMailRepository;

@Service
public class TeamMailService {

    private final TeamMailRepository teamMailRepository;

    public TeamMailService(TeamMailRepository teamMailRepository) {
        this.teamMailRepository = teamMailRepository;
    }

    // userId로 내가 속한 부서 대표메일 리스트 가져오기
    public List<String> findTeamMailsByUserId(String userId) {
        // teamMailRepository에서 쿼리 메서드 호출
        return teamMailRepository.findTeamMailsByUserId(userId);
    }
}
