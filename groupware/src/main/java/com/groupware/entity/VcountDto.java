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
@Table(name="VCOUNT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VcountDto {

	@Id  // 기본키 설정
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int lineno;
	
	@Column(nullable=false)
	private int edsmno;
	
	@Column(nullable=false)
	private int empno;
	
	@Column(length=200,nullable=false)
	private String edpro;
	
	@Column(length=200,nullable=false)
	private String edst;
	
	@CreationTimestamp
	private Timestamp eddate;

	
}
