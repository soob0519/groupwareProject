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
@Table(name="EMP")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpDto {

	@Id  // 기본키 설정
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int empno;
	
	@Column(length=200,nullable=false)
	private String userid;
	
	@Column(length=200,nullable=false)
	private String pass;
	
	@Column(length=200,nullable=false)
	private String name;
	
	//부서 코드테이블
	@Column(length=200,nullable=false)
	private String dept;
	
	//직급 코드테이블
	@Column(length=200,nullable=false)
	private String position;
	
	// 등록일
	@CreationTimestamp
	private Timestamp rdate;
	
	// 입사일
	private Timestamp jdate;
	
	//퇴사일
	private Timestamp qdate;
	
	@Column(length=2000)
	private String addr;
	
	@Column(length=200)
	private String email;
	
	@Column(length=200)
	private String phone;
	
	@Column(length=200, nullable=false)
	private String state = "Y";
	
}
