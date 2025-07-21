package com.groupware.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.groupware.dto.UserDto;
import com.groupware.entity.CodeDto;
import com.groupware.entity.EmpDto;
import com.groupware.repository.CodeRepository;
import com.groupware.repository.EmpRepository;


@Service
public class UserService {

    private final EmpRepository empRepository;
    private final CodeRepository codeRepository;

    public UserService(EmpRepository empRepository,CodeRepository codeRepository) {

        this.empRepository = empRepository;
        this.codeRepository = codeRepository;
    }

    //메일 작성 - 메일이름작성시 이메일 자동완성
    public List<UserDto> searchUsersWithDept(String query) {
        List<EmpDto> emps = empRepository.findByNameContaining(query); // empRepository로 연동 
        List<UserDto> results = new ArrayList<>();
        for (EmpDto emp : emps) {
            String deptName = "-";
            if (emp.getDept() != null) {
                CodeDto code = codeRepository.findById(emp.getDept()).orElse(null);
                if (code != null && "Y".equals(code.getState()) && code.getNcode() != null) {
                    deptName = code.getNcode();
                }
            }
            UserDto dto = new UserDto();
            dto.setUserid(emp.getUserid());
            dto.setName(emp.getName() + " (" + deptName + ")");
            dto.setDepartment(deptName);
            dto.setEmail(emp.getEmail());
            results.add(dto);
        }
        return results;
    }

}
