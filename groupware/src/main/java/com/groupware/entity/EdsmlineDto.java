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
@Table(name="EDSMLINE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EdsmlineDto {

	@Id  // 기본키 설정
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int lineno;
	
	@Column(nullable=false)
	private int edsmno;
	
	@Column(length=4000,nullable=false)
	private int empno;
	
	@Column(length=200,nullable=false)
	private String edpro;
	
	@Column(nullable=false)
	private int edsmst;
	
	@CreationTimestamp
	private Timestamp eddate;
	
	@Column(length=2000,nullable=false)
	private String edopni;

	
}
