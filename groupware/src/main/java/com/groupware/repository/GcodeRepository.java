package com.groupware.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.groupware.entity.GcodeDto;


public interface GcodeRepository extends JpaRepository<GcodeDto,Integer> {


}