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
	
	@Column(length=4000,nullable=false)
	private String edcont;
	
	@Column(nullable=false)
	private int edopti;
	
	@CreationTimestamp
	private Timestamp wdate;
	
	@CreationTimestamp
	private Timestamp udate;
	
	@Column(nullable=false)
	private int edst;
	
	@CreationTimestamp
	private Timestamp apdate;
}
