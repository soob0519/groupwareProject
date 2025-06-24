package com.groupware.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.groupware.entity.EmpDto;
import com.groupware.service.EmpService;

@RestController
@RequestMapping("/emp")
public class EmpController {
	
	public final EmpService empService;
	public EmpController(EmpService empService) {
		this.empService = empService;
	}
	
	/**
	 * 사원 등록 처리
	 */
	@GetMapping("/empWrite")
	public ModelAndView empWrite() {
		
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/empWrite");
		return model;
	}

	/**
	 * 사원 등록 처리
	 */
	@PostMapping("/empSave")
	public String empSave(EmpDto dto) {
	
		String msg = "1";
		EmpDto dto1 = empService.save(dto);
		if( dto1 == null ) {
			msg = "2";
		}
		return msg;
	}
	
	/**
	 * 사원 목록
	 */
	@GetMapping("/empList")
	public ModelAndView empList(@RequestParam(defaultValue = "0") int page,
            					 @RequestParam(defaultValue = "10") int size) {
		
		Page<EmpDto> result = empService.list(page, size);
		ModelAndView model = new ModelAndView("/admin/empList");
		model.addObject("list", result.getContent());
		model.addObject("page", result);
		return model;
	}
	
	/**
	 * 사원 삭제
	 */
	@PostMapping("/empDelete")
	public String empDelete(int empno) {
		String msg = "1";
		boolean result = empService.deleteById(empno);
		if( result == true) {
			msg = "1";
		} else {
			msg = "2";
		}
		return msg;
	}
	
	
}
