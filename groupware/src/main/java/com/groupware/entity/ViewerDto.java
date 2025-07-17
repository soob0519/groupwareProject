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

	@Id  // 기본키 설정1
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int viewno;
	
	@Column(nullable=false)
	private int edsmno;
	
	@Column(nullable=false)
	private int empno;
	
}
