package com.groupware.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TEAM_MAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMailDto {

    @Id
    @Column(name = "team_id", length = 50)
    private String teamId; // 부서 코드

    @Column(name = "team_name", length = 100, nullable = false)
    private String teamName; // 부서 이름

    @Column(name = "team_email", length = 100, nullable = false)
    private String teamMail;// 부서 대표메일 
}

