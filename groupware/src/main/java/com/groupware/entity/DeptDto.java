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
@Table(name="DEPT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeptDto {

	@Id
	@Column(nullable=false)
	private int deptno;
	
	@Column(length=200,nullable=false)
	private String deptnm;
	
	@Column(length=200,nullable=false)
	private String depttm;
	
	@Column(length=200,nullable=false)
	private String deptst;
	
}
