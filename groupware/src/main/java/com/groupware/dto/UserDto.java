package com.groupware.dto;

public class UserDto {
    private String userid;
    private String name;
    private String department;
    private String email;

    // 기본 생성자
    public UserDto() {}

    // 생성자
    public UserDto(String userid, String name, String department, String email) {
        this.userid = userid;
        this.name = name;
        this.department = department;
        this.email = email;
    }

    // Getter & Setter
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

