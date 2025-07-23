package com.groupware.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.groupware.entity.CodeDto;
import com.groupware.entity.EmpDto;
import com.groupware.service.CodeService;
import com.groupware.service.EmpService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/emp")
public class EmpController {
	
	public final EmpService empService;
	public final CodeService codeService;
	public EmpController(EmpService empService,
							CodeService codeService	)	 {
		this.empService = empService;
		this.codeService = codeService;
	}
	
	/**
	 * 사원 등록 처리1
	 */
	@GetMapping("/empWrite")
	public ModelAndView empWrite(HttpSession session,
								Model model,
								@RequestParam(defaultValue = "0") int page,
								@RequestParam(defaultValue = "10") int size) {
		
		String dept = (String) session.getAttribute("dept");
		if (dept == null || !dept.equals("B20003")) {
	        return new ModelAndView("redirect:/login/login");
	    }
		Integer empno = (Integer)session.getAttribute("empno");
		if (empno == null) {
	        return new ModelAndView("redirect:/login/login");
	    }
		EmpDto emp = empService.findById(empno);
	    if (emp == null || "N".equals(emp.getState())) {
	        session.invalidate();
	        return new ModelAndView("redirect:/login/login");
	    }
		
		ModelAndView model1 = new ModelAndView();
		List<CodeDto> result2 = codeService.list();
		
		model1.addObject("list2", result2);
		model1.setViewName("/admin/empWrite");
		
		return model1;
	}

	/**
	 * 사원 등록 처리
	 */
	@PostMapping("/empSave")
	public String empSave(EmpDto dto) {
	 System.out.println("dddddddddddddddddd");
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
	public ModelAndView empList(HttpSession session,
								@RequestParam(defaultValue = "0") int page, 
								@RequestParam(defaultValue = "10") int size,
								@RequestParam(required = false, defaultValue = "") String searchType,
								@RequestParam(required = false, defaultValue = "") String keyword,
								@RequestParam(required = false, defaultValue = "") String state) {
		
		String dept = (String) session.getAttribute("dept");
		if (dept == null || !dept.equals("B20003")) {
	        return new ModelAndView("redirect:/login/login");
	    }
		Integer empno = (Integer)session.getAttribute("empno");
		if (empno == null) {
	        return new ModelAndView("redirect:/login/login");
	    }
		EmpDto emp = empService.findById(empno);
	    if (emp == null || "N".equals(emp.getState())) {
	        session.invalidate();
	        return new ModelAndView("redirect:/login/login");
	    }
		
		Page<EmpDto> result;

		if (state != null && (state.equals("Y") || state.equals("N"))) {
			// 상태에 따라 필터링 + 검색 조합 필요하면 추가 구현 가능
			if (searchType != null && keyword != null && !keyword.isEmpty()) {
				if ("name".equals(searchType)) {
					result = empService.searchByNameAndState(keyword, state, page, size);
				} else if ("deptname".equals(searchType)) {
					result = empService.searchByDeptAndState(keyword, state, page, size);
				} else {
					result = empService.listByState(state, page, size);
				}
			} else {
			result = empService.listByState(state, page, size);
			}
		} else {
		// 상태 필터 없이 기존 검색 로직
			if (searchType != null && keyword != null && !keyword.isEmpty()) {
				if ("name".equals(searchType)) {
					result = empService.searchByName(keyword, page, size);
				} else if ("deptname".equals(searchType)) {
					result = empService.searchByDept(keyword, page, size);
				} else {
					result = empService.list(page, size);
				}
			} else {
				result = empService.list(page, size);
			}
		}		
		
		List<CodeDto> result2 = codeService.list();
		
		ModelAndView model = new ModelAndView("/admin/empList");
		model.addObject("list", result.getContent());
		model.addObject("page", result);
		model.addObject("list2", result2);
		model.addObject("searchType", searchType);
		model.addObject("keyword", keyword);
		model.addObject("state", state);  // 선택된 상태 유지
		
		return model;
	}

	/**
	 * 사원 상태 변경
	 */
	@PostMapping("/changeState")
	@ResponseBody
	public String changeState(@RequestParam int empno) {
		empService.toggleState(empno);
	    return "success";
	}
	
	
	/**
	 * 사원 상세정보
	 */
	@GetMapping("/{empno}")
	public ModelAndView findById(HttpSession session,
								@PathVariable Integer empno,
								 @RequestParam(defaultValue = "0") int page,
								 @RequestParam(defaultValue = "10") int size) {
		
		String dept = (String) session.getAttribute("dept");
		if (dept == null || !dept.equals("B20003")) {
	        return new ModelAndView("redirect:/login/login");
	    }

		if (empno == null) {
	        return new ModelAndView("redirect:/login/login");
	    }
		EmpDto emp = empService.findById(empno);
	    
		ModelAndView model = new ModelAndView();
		EmpDto dto = empService.getFindById(empno);
		model.setViewName("/admin/empModify");
		model.addObject("dto", dto);
		
		return model;
	}
	
	/**
	 * 사원 수정
	 */
	@PostMapping("/empUpdate")
	public String empModify(EmpDto dto) {
		String msg = "1";
		EmpDto dto1 = empService.save(dto);
		if( dto1 == null ) {
			msg = "2";
		}
		return msg;
	}
	
	
	/**
	 * 사원 조직도
	 */
	@GetMapping("/empOrgani")
	public ModelAndView empOrgani(HttpSession session,
								@RequestParam(defaultValue = "0") int page,
								@RequestParam(defaultValue = "10") int size) {
		Integer empno = (Integer)session.getAttribute("empno");
		if (empno == null) {
	        return new ModelAndView("redirect:/login/login");
	    }
		EmpDto emp = empService.findById(empno);
	    if (emp == null || "N".equals(emp.getState())) {
	        session.invalidate();
	        return new ModelAndView("redirect:/login/login");
	    }
		
		ModelAndView model = new ModelAndView();
		
		List<EmpDto> empList = empService.findAll();
		System.out.println("사원 수: " + empList.size());
		List<CodeDto> result2 = codeService.list();
		Map<String, List<EmpDto>> deptMap = empList.stream()
		        .collect(Collectors.groupingBy(EmpDto::getDept));

		model.addObject("deptMap", deptMap);
		model.addObject("list", empList);
		model.addObject("list2", result2);
		model.setViewName("/admin/empList2");
		
		return model;
	}
	
	/**
	 * 사원 상세정보
	 */
	@GetMapping("/detail/{empno}")
	public ModelAndView detail(HttpSession session,@PathVariable Integer empno) {
		
		ModelAndView model = new ModelAndView();
		EmpDto dto = empService.getFindById(empno);
		
		model.setViewName("/admin/empList2Detail");
		model.addObject("dto", dto);
		
		return model;
	}
	
}
