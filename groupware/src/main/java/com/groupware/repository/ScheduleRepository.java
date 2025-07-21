package com.groupware.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.groupware.entity.ScheduleDto;

import jakarta.transaction.Transactional;

public interface ScheduleRepository extends JpaRepository<ScheduleDto,Integer> {
	
	List<ScheduleDto> findByStartdateBetween(LocalDate start, LocalDate end);

	@Query(value=" select    scheno		\r\n"
			+ "				,sche_chk	\r\n"
			+ "				,cal_chk	\r\n"
			+ "				,schetitle	\r\n"
			+ "				,schecont	\r\n"
			+ "				,startdate	\r\n"
			+ "				,enddate	\r\n"
			+ "				,starttime	\r\n"
			+ "				,endtime	\r\n"
			+ "				,to_char(rdate,'dd') dd	\r\n"
			+ "				,wrtnm		\r\n"
			+ "				,participant			\r\n"
			+ "				,sharer "
			+ "		from "
			+ "				schedule "
	        + "		WHERE "
	        + "				rdate "
	        + "		BETWEEN "
	        + "				TO_DATE(:rdate || '-01', 'yyyy-mm-dd') "
	        + "		AND "
	        + "				LAST_DAY(TO_DATE(:rdate || '-01', 'yyyy-mm-dd'))", nativeQuery = true)
    List<Map> calList(String rdate);
	
}
