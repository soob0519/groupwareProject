package com.groupware.entity;

import java.time.LocalDate;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

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
@Table(name = "SCHEDULE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int scheno;

    @Column(nullable = false)
    private int sche_chk;

    @Column(nullable = false)
    private int cal_chk;

    @Column(nullable = false, length = 200)
    private String schetitle;

    @Column(length = 4000)
    private String schecont;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column
    private LocalDate startdate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column
    private LocalDate enddate;

    @Column(length = 20)
    private String starttime;

    @Column(length = 20)
    private String endtime;

    @CreationTimestamp
    @Column(updatable = false)
    private Date rdate;

    @Column(nullable = false)
    private String wrtnm;

    @Column
    private Integer participant;

    @Column
    private Integer sharer;
}							
							
							 
							
							
							
							
							