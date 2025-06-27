package com.groupware.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.groupware.entity.DeptDto;
import com.groupware.entity.EmpDto;
import com.groupware.entity.PositionDto;
import com.groupware.service.DeptService;
import com.groupware.service.EmpService;
import com.groupware.service.PositionService;

@RestController
@RequestMapping("/emp")
public class EmpController {
	
	public final EmpService empService;
	private final DeptService deptService;
    private final PositionService positionService;
	public EmpController(EmpService empService,
						DeptService deptService,
						PositionService positionService)	 {
		this.empService = empService;
		this.deptService = deptService;
		this.positionService = positionService;
	}
	
	/**
	 * 사원 등록 처리
	 */
	@GetMapping("/empWrite")
	public ModelAndView empWrite(@RequestParam(defaultValue = "0") int page,
								@RequestParam(defaultValue = "10") int size) {
		
		ModelAndView model = new ModelAndView();
		Page<PositionDto> result2 = positionService.list(page, size);
		Page<DeptDto> result3 = deptService.list(page, size);
		
		model.addObject("list2", result2.getContent());
		model.addObject("list3", result3.getContent());
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
		Page<PositionDto> result2 = positionService.list(page, size);
		Page<DeptDto> result3 = deptService.list(page, size);
		
		ModelAndView model = new ModelAndView("/admin/empList");
		
		model.addObject("list", result.getContent());
		model.addObject("list2", result2.getContent());
		model.addObject("list3", result3.getContent());
		model.addObject("page", result);
		return model;
	}
	
	/**
	 * 사원 상세정보
	 */
	@GetMapping("/{empno}")
	public ModelAndView findById(@PathVariable Integer empno,
								 @RequestParam(defaultValue = "0") int page,
								 @RequestParam(defaultValue = "10") int size) {
		
		Page<PositionDto> result2 = positionService.list(page, size);
		Page<DeptDto> result3 = deptService.list(page, size);
		
		ModelAndView model = new ModelAndView();
		EmpDto dto = empService.getFindById(empno);
		model.addObject("list2", result2.getContent());
		model.addObject("list3", result3.getContent());
		model.setViewName("/admin/empModify");
		model.addObject("dto", dto);
		
		return model;
	}
	
	/**
	 * 사원 수정
	 */
	@PostMapping("/empModify")
	public String empModify(EmpDto dto) {
		String msg = "1";
		EmpDto dto1 = empService.save(dto);
		if( dto1 == null ) {
			msg = "2";
		}
		return msg;
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
