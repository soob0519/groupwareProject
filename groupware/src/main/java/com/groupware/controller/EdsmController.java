package com.groupware.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.groupware.entity.CodeDto;
import com.groupware.entity.EdsmDto;
import com.groupware.entity.EdsmlineDto;
import com.groupware.entity.EmpDto;
import com.groupware.service.CodeService;
import com.groupware.service.EdsmService;
import com.groupware.service.EdsmlineService;
import com.groupware.service.EmpService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/edsm")
public class EdsmController {
	
	public final EdsmService edsmService;
	public final EmpService empService;
	public final CodeService codeService;
	public final EdsmlineService edsmlineService;
	public EdsmController(	EdsmService edsmService,
							EmpService empService,
							CodeService codeService,
							EdsmlineService edsmlineService) {
		this.edsmService = edsmService;
		this.empService = empService;
		this.codeService = codeService;
		this.edsmlineService = edsmlineService;
	}
	
	@GetMapping
	public ModelAndView main() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/edsm/edsmMain");
		return model;
	}
	
	/**
	 * 기안작성
	 */
	@GetMapping("/edsmWrite")
	public ModelAndView edsmWrite(HttpSession session,
								  @RequestParam(defaultValue = "0") int page,
								  @RequestParam(defaultValue = "10") int size) {
		
		String userId = (String) session.getAttribute("userId");
		userId = "parkdh"; // 테스트용 하드코딩
		if (userId == null || userId.equals("")) {
			return new ModelAndView("redirect:/homepage");
		}		

		ModelAndView model = new ModelAndView();
		model.addObject("user", userId);

		List<EmpDto> result1 = empService.findAll(); // 결재자용
		List<CodeDto> result2 = codeService.list();  // 코드 목록
		
		Map<String, List<EmpDto>> deptMap = result1.stream()
				.collect(Collectors.groupingBy(EmpDto::getDept));

		model.addObject("list1", result1);
		model.addObject("list2", result2);
		model.addObject("deptMap", deptMap);

		model.setViewName("/edsm/edsmWrite");

		return model;
	}
	
	/**
	 * 사원 조직도 결재자 선택창
	 */
	@GetMapping("/empOrgani1")
	public ModelAndView empOrgani(@RequestParam(defaultValue = "0") int page,
								@RequestParam(defaultValue = "10") int size) {
		
		ModelAndView model = new ModelAndView();
		
		List<EmpDto> empList = empService.findAll();
		System.out.println("사원 수: " + empList.size());
		List<CodeDto> result2 = codeService.list();
		Map<String, List<EmpDto>> deptMap = empList.stream()
		        .collect(Collectors.groupingBy(EmpDto::getDept));

		model.addObject("deptMap", deptMap);
		model.addObject("list", empList);
		model.addObject("list2", result2);
		model.setViewName("/edsm/empList1");
		
		return model;
	}
	
	/**
	 * 기안 저장
	 */
	@PostMapping("/edsmSave")
	@ResponseBody
	public String submitDocument(EdsmDto dto, HttpSession session) {
	    //int empno = (int) session.getAttribute("empno");
	    int empno = 2002;
	    dto.setEmpno(empno);
	    dto.setEdst("F60001");     // 결재 진행 중
	    dto.setIsdraft("N");       // 임시보관 아님

	    // 1. 문서 저장
	    EdsmDto saved = edsmService.save(dto);

	    // 2. 결재라인 저장
	    String[] approvers = dto.getApproverIds().split(",");
	    for (int i = 0; i < approvers.length; i++) {
	        int approverEmpno = Integer.parseInt(approvers[i]);

	        EdsmlineDto line = EdsmlineDto.builder()
	                .edsmno(saved.getEdsmno())
	                .empno(approverEmpno)
	                .edpro(String.valueOf(i + 1))
	                .edst(i == 0 ? "C30002" : "C30001") // 첫 사람은 진행중, 나머지는 대기
	                .build();

	        edsmlineService.save(line);
	    }

	    return "1";
	}
	
	/**
	 * 기안 임시 저장
	 */
	@PostMapping("/edsmIsdraft")
	@ResponseBody
	public String isdraftDocument(EdsmDto dto, HttpSession session) {
	    //int empno = (int) session.getAttribute("empno");
	    int empno = 2002;
	    dto.setEmpno(empno);
	    dto.setEdst("F60001");     // 결재 진행 중
	    dto.setIsdraft("Y");

	    // 1. 문서 저장
	    EdsmDto saved = edsmService.save(dto);

	    // 2. 결재라인 저장
	    String[] approvers = dto.getApproverIds().split(",");
	    for (int i = 0; i < approvers.length; i++) {
	        int approverEmpno = Integer.parseInt(approvers[i]);

	        // 임시보관일 경우 모두 대기로 저장
	        String lineStatus = "C30001"; // 대기
	        EdsmlineDto line = EdsmlineDto.builder()
	                .edsmno(saved.getEdsmno())
	                .empno(approverEmpno)
	                .edpro(String.valueOf(i + 1))
	                .edst(lineStatus)
	                .build();

	        edsmlineService.save(line);
	    }

	    return "1";
	}
	

	
	
}
