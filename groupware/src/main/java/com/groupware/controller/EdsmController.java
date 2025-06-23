package com.groupware.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.groupware.service.EdsmService;

@RestController
@RequestMapping("/edsm")
public class EdsmController {
	
	public final EdsmService edsmService;
	public EdsmController(EdsmService edsmService) {
		this.edsmService = edsmService;
	}
	
	@GetMapping
	public ModelAndView main() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/edsm/edsmWait");
		return model;
	}
	
	
}
