package com.groupware.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@GetMapping
	public ModelAndView main() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/adminMain");
		return model;
	}
	
	@GetMapping("/deptWrite")
	public ModelAndView deptWrite() {
		
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/deptWrite");
		return model;
	}
	
	@GetMapping("/deptList")
	public ModelAndView deptList() {
		
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/deptList");
		return model;
	}
	
	@GetMapping("/empWrite")
	public ModelAndView empWrite() {
		
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/empWrite");
		return model;
	}
	
	@GetMapping("/empList")
	public ModelAndView empList() {
		
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/empList");
		return model;
	}
	
	@GetMapping("/posWrite")
	public ModelAndView posWrite() {
		
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/posWrite");
		return model;
	}
	
	@GetMapping("/posList")
	public ModelAndView posList() {
		
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/posList");
		return model;
	}
	
	@GetMapping("/codeWrite")
	public ModelAndView codeWrite() {
		
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/codeWrite");
		return model;
	}
	
	@GetMapping("/codeList")
	public ModelAndView codeList() {
		
		ModelAndView model = new ModelAndView();
		model.setViewName("/admin/codeList");
		return model;
	}
	
}
