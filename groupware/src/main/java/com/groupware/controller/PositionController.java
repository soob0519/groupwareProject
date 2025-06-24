package com.groupware.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.groupware.entity.PositionDto;
import com.groupware.service.PositionService;


@RestController
@RequestMapping("/position")
public class PositionController {
	
	public final PositionService positionService;
	public PositionController(PositionService positionService) {
		this.positionService = positionService;
	}
	
	@GetMapping("/posWrite")
	public ModelAndView posWrite() {
		
		int code = positionService.posnoNew();
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/posWrite");
		model.addObject("code", code);
		return model;
	}
	
	/**
	 * 코드 등록 처리
	 */
	@PostMapping("/posSave")
	public String posSave(PositionDto dto) {
	
		String msg = "1";
		PositionDto dto1 = positionService.save(dto);
		if( dto1 == null ) {
			msg = "2";
		}
		return msg;
	}
	
	/**
	 * 코드 목록
	 */
	@GetMapping("/posList")
	public ModelAndView posList(@RequestParam(defaultValue = "0") int page,
            					 @RequestParam(defaultValue = "10") int size) {
		
		Page<PositionDto> result = positionService.list(page, size);
		ModelAndView model = new ModelAndView("/admin/posList");
		model.addObject("list", result.getContent());
		model.addObject("page", result);
		return model;
	}
	
	/**
	 * 코드 삭제
	 */
	@PostMapping("/posDelete")
	public String posDelete(int posno) {
		String msg = "1";
		boolean result = positionService.deleteById(posno);
		if( result == true) {
			msg = "1";
		} else {
			msg = "2";
		}
		return msg;
	}
	
}
