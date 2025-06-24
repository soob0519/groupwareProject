package com.groupware.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.groupware.entity.GcodeDto;
import com.groupware.service.GcodeService;


@RestController
@RequestMapping("/gcode")
public class GcodeController {
	
	public final GcodeService gcodeService;
	public GcodeController(GcodeService gcodeService) {
		this.gcodeService = gcodeService;
	}
	
	
	/**
	 * 코드 등록 화면
	 */
	@GetMapping("/codeWrite")
	public ModelAndView codeWrite() {
		
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/codeWrite");
		return model;
	}
	
	/**
	 * 코드 등록 처리
	 */
	@PostMapping("/codeSave")
	public String codeSave(GcodeDto dto) {
	
		String msg = "1";
		GcodeDto dto1 = gcodeService.save(dto);
		if( dto1 == null ) {
			msg = "2";
		}
		return msg;
	}
	
	/**
	 * 코드 목록
	 */
	@GetMapping("/codeList")
	public ModelAndView codeList(@RequestParam(defaultValue = "0") int page,
            					 @RequestParam(defaultValue = "10") int size) {
		
		Page<GcodeDto> result = gcodeService.list(page, size);
		ModelAndView model = new ModelAndView("/admin/codeList");
		model.addObject("list", result.getContent());
		model.addObject("page", result);
		return model;
	}
	
	/**
	 * 코드 삭제
	 */
	@PostMapping("/codeDelete")
	public String codeDelete(int cdno) {
		String msg = "1";
		boolean result = gcodeService.deleteById(cdno);
		if( result == true) {
			msg = "1";
		} else {
			msg = "2";
		}
		return msg;
	}
	
}
