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
@Table(name="CODE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeDto {
	
	//부모코드
	@Column(length=200)
	private String pcode;
	
	// 코드값
	@Column(length=200)
	private String vcode;
	
	// 코드이름
	@Column(length=200)
	private String ncode;
	
	// 순서
	@Column
	private int dorder;
	
	//사용 여부
	@Column(length=200)
	private String state;
	
	// 비고
	@Column(length=200)
	private String remark;
	
	// 사용시 코드
	@Id
	@Column(length=200)
	private String ucode;
	
}
