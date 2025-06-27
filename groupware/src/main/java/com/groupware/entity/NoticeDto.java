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
@Table(name="notice")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDto {
	
	@Id //기본키 설정 (고유번호)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int   	  ntcno;
	
	// 비밀번호
	@Column(nullable = false, length = 200)
	private String    ntcps;
	
	// 제목
	@Column(nullable = false, length = 200)
	private String    ntctt;
	
	// 작성자(관리자)
	@Column(nullable = false, length = 200)
	private String    ntcwr;
	
	// 유형 일반/필수
	@Column(nullable = false, length = 50)
	private String 	  ntcca;
	
	// 게시일
	@CreationTimestamp
	private Timestamp ntcrd;
	
	// 내용
	@Column(length = 5000)
	private String    ntcct;
	
	// 조회수
	private int ntcht = 0;
}