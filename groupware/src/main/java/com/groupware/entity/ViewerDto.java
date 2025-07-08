package com.groupware.entity;


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
@Table(name="VIEWER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewerDto {

	@Id  // 기본키 설정
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int viewno;
	
	@Column(nullable=false)
	private int edsmno;
	
	@Column(nullable=false)
	private int empno;
	
	// 부서 코드테이블
	@Column(length=200,nullable=false)
	private String dept;
	
	// 수신자 공유자 구분 코드테이블
	@Column(length=200,nullable=false)
	private String viewtype;

	
}
