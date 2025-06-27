package com.groupware.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ZEC_Project.entity.ScheduleDto;

public interface ScheduleRepository extends JpaRepository<ScheduleDto,Integer> {

	@Query(value=" select    sdno			\r\n"
			+ "				,calcheck		\r\n"
			+ "				,deptno			\r\n"
			+ "				,description	\r\n"
			+ "				,sdcheck		\r\n"
			+ "				,to_char(sddate,'dd') dd	\r\n"
			+ "				,sdtime			\r\n"
			+ "				,sdtitle		\r\n"
			+ "				,userid "
			+ "		from "
			+ "				schedule "
			+ "		where"
			+ "				to_date(to_char(sddate,'yyyy-mm'),'yyyy-mm') "
			+ "				= "
			+ "				to_date( :sddate ,'yyyy-mm')",nativeQuery=true)
	public List<Map> calList(String sddate);
	
}
