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
@Table(name="FAVOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavorDto {

	@Id  // 기본키 설정
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int favorno;
	
	// 누른 사원
	@Column(nullable=false)
	private int empno;
	
	// 즐겨찾기 사원
	@Column(nullable=false)
	private int fempno;

	
}
