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
	
	// 일정고유번호
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int scheno;

	// 일정구분
	@Column(nullable=false)
	private int sche_chk;

	// 캘린더구분
	@Column(nullable=false)
	private int cal_chk;

	// 제목
	@Column(nullable=false, length=200)
	private String schetitle;

	// 상세내용
	@Column(nullable=false, length=4000)
	private String schecont;

	// 시작날짜
	@Column(nullable=false, length=20)
	private String startdate;
	
	// 종료날짜
	@Column(nullable=false, length=20)
	private String enddate;
	
	// 시작시간
	@Column(length=20)
	private String starttime;

	// 종료시간
	@Column(length=20)
	private String endtime;

	// 등록일
	@Column
	private Date rdate;
	
	// 작성자
	@Column(nullable=false)
	private int wrtnm;
	
	// 참여자
	@Column()
	private int participant;
	
	// 공유자
	@Column()
	private int sharer;
	
	
}
							
							
							
							
							
							
							
							