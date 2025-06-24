package com.groupware.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.groupware.entity.DeptDto;
import com.groupware.service.DeptService;

@RestController
@RequestMapping("/dept")
public class DeptController {
	
	public final DeptService deptService;
	public DeptController(DeptService deptService) {
		this.deptService = deptService;
	}
	
	/**
	 * 부서 등록 화면
	 */
	@GetMapping("/deptWrite")
	public ModelAndView deptWrite() {
		
		int code = deptService.deptnoNew();
		
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/deptWrite");
		model.addObject("code", code);
		return model;
	}
	

	/**
	 * 부서 등록 처리
	 */
	@PostMapping("/deptSave")
	public String deptSave(DeptDto dto) {
	
		String msg = "1";
		DeptDto dto1 = deptService.save(dto);
		if( dto1 == null ) {
			msg = "2";
		}
		return msg;
	}
	
	/**
	 * 부서 목록
	 */
	@GetMapping("/deptList")
	public ModelAndView deptList(@RequestParam(defaultValue = "0") int page,
            					 @RequestParam(defaultValue = "10") int size) {
		
		Page<DeptDto> result = deptService.list(page, size);
		ModelAndView model = new ModelAndView("/admin/deptList");
		model.addObject("list", result.getContent());
		model.addObject("page", result);
		return model;
	}
	
	/**
	 * 부서 삭제
	 */
	@PostMapping("/deptDelete")
	public String deptDelete(int deptno) {
		String msg = "1";
		boolean result = deptService.deleteById(deptno);
		if( result == true) {
			msg = "1";
		} else {
			msg = "2";
		}
		return msg;
	}
	
	
}
