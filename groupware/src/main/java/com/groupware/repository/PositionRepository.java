package com.groupware.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.groupware.entity.PositionDto;



public interface PositionRepository extends JpaRepository<PositionDto,Integer> {
	
	@Query(value="select nvl(max(posno),1000)+1 from position",nativeQuery=true)
	int findByNative();

}