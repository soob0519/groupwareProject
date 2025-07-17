package com.groupware.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Transient;

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
@Table(name="EDSM")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EdsmDto {

	@Id  // 기본키 설정
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int edsmno;
	
	@Column(length=200,nullable=false)
	private String edtitle;
	
	@Column(length=4000)
	private String edcont;
	
	//휴가종류 코드테이블
	@Column(length=200)
	private String vtype;
	
	//결재상태 코드테이블
	@Column(length=200,nullable=false)
	private String edst;
	
	// 작성일
	@CreationTimestamp
	private Timestamp wdate;
	
	// 수정일
	@UpdateTimestamp
	private Timestamp udate;
	
	// 승인완결날짜
	private Timestamp apdate;
	
	// 휴가날짜 시작일
	private Timestamp svac;
	
	// 휴가날짜 종료일
	private Timestamp evac;
	
	// 작성자
	@Column(nullable=false)
	private int empno;
	
	//반려의견
	@Column(length=1000)
	private String edcomment;
	
	//첨부문서
	@Column(length=4000)
	private String attachment;
	
	//임시보관 구분
	@Column(length = 1)
	private String isdraft;
	
	@Transient
	private String approverIds;
	
}
