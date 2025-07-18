package com.groupware.entity;

import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Gnotice")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GnoticeDto {
	
	@Id //기본키 설정 (고유번호)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int   	  gntcno;
	
	// 제목
	@Column(nullable = false, length = 200)
	private String    gntctt;
	
	// 부서
	@Column(nullable = false, length = 50)
	private String 	  deptno;
	
	// 게시일
	@CreationTimestamp
	private Timestamp gntcrd;
	
	// 내용
	@Column(length = 5000)
	private String    gntcct;
	
	// 조회수
	private int gntcht = 0;
}