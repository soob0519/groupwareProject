package com.groupware.entity;

import java.util.Date;

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
@Table(name="SCHEDULE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int sdno;

	// 일정구분
	@Column(nullable=false)
	private int sdcheck;

	// 날짜
	@Column(nullable=false)
	private Date sddate;

	// 등록시간
	@Column
	private String sdtime;

	// 시작시간
	@Column
	private String starttime;

	// 종료시간
	@Column
	private Date endtime;

	// 부서고유번호
	@Column(nullable=false)
	private int deptno;

	// 제목
	@Column(nullable=false)
	private String sdtitle;

	// 상세
	@Column(nullable=false)
	private String description;

	// 캘린더구분
	@Column(nullable=false)
	private int calcheck;

	// 작성자 아이디
	@Column(nullable=false)
	private String userid;
	

}
							
							
							
							
							
							
							
							