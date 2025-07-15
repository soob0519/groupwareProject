package com.groupware.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.groupware.entity.VcountDto;


public interface VcountRepository extends JpaRepository<VcountDto,Integer> {

}
