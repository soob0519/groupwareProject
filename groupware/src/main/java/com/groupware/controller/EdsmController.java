package com.groupware.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.groupware.entity.FavorDto;
import com.groupware.entity.ViewerDto;
import com.groupware.service.CodeService;
import com.groupware.service.EdsmService;
import com.groupware.service.EdsmlineService;
import com.groupware.service.EmpService;
import com.groupware.service.FavorService;
import com.groupware.service.ViewerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/edsm")
public class EdsmController {
	
	public final EdsmService edsmService;
	public final EmpService empService;
	public final CodeService codeService;
	public final EdsmlineService edsmlineService;
	public final ViewerService viewerService;
	public final FavorService favorService;
	public EdsmController(	EdsmService edsmService,
							EmpService empService,
							CodeService codeService,
							EdsmlineService edsmlineService,
							ViewerService viewerService,
							FavorService favorService) {
		this.edsmService = edsmService;
		this.empService = empService;
		this.codeService = codeService;
		this.edsmlineService = edsmlineService;
		this.viewerService = viewerService;
		this.favorService = favorService;
		
	}

	
	/**
	 * 기안작성
	 */
	@GetMapping("/edsmWrite")
	public ModelAndView edsmWrite(HttpSession session,
								  @RequestParam(defaultValue = "0") int page,
								  @RequestParam(defaultValue = "10") int size) {
		
		String userId = (String) session.getAttribute("userid");
		int empno = (int) session.getAttribute("empno");
	    if (userId == null || userId.equals("")) {
	        return new ModelAndView("redirect:/login/login");
	    }	    

	    ModelAndView model = new ModelAndView("/edsm/edsmWrite");
	    model.addObject("user", userId);

	    List<EmpDto> empList = empService.findAll();
	    List<CodeDto> codeList = codeService.list();

	    Map<String, List<EmpDto>> deptMap = empList.stream()
	            .collect(Collectors.groupingBy(EmpDto::getDept));

	    // 포지션 정렬
	    deptMap.values().forEach(list ->
	        list.sort(Comparator.comparing(EmpDto::getPosition))
	    );
	    
	    //즐겨찾기
	    List<Integer> favList = Optional.ofNullable(favorService.findFavoritesByEmpno(empno))
	    	    .orElseGet(ArrayList::new);

    	List<EmpDto> favEmpList = empList.stream()
    	    .filter(emp -> favList.contains(Integer.valueOf(emp.getEmpno())))
    	    .collect(Collectors.toList());
	    
	    model.addObject("favEmpList", favEmpList);
	    model.addObject("favList", favList);
	    model.addObject("list1", empList);
	    model.addObject("list2", codeList);
	    model.addObject("deptMap", deptMap);
	    return model;
	}
	
	/**
	 * 사원 조직도 결재자 선택창
	 */
	@GetMapping("/empOrgani1")
	public ModelAndView empOrgani(HttpSession session,
									@RequestParam(defaultValue = "0") int page,
									@RequestParam(defaultValue = "10") int size) {
		
		int empno = (int)session.getAttribute("empno");
	    
		ModelAndView model = new ModelAndView();
		
		List<EmpDto> empList = empService.findAll();		
	    List<CodeDto> result2 = codeService.list();

	    //부서별 그룹핑
	    Map<String, List<EmpDto>> deptMap = empList.stream()
	        .collect(Collectors.groupingBy(EmpDto::getDept));
	    
	    // 즐겨찾기
	    List<Integer> favList = favorService.findFavoritesByEmpno(empno);
	    
	    if(favList == null) {
	        favList = new ArrayList<>();
	    }
	    
	    //직급별 정리
	    deptMap.values().forEach(list ->
	        list.sort(Comparator.comparing(EmpDto::getPosition))
	    );

	    model.addObject("deptMap", deptMap);
	    model.addObject("list", empList);
	    model.addObject("list2", result2);
	    model.addObject("favList", favList);
	    model.setViewName("/edsm/empList1");

	    return model;
	}
	
	/**
	 * 사원 조직도 공유자 선택창
	 */
	@GetMapping("/empOrgani2")
	public ModelAndView empOrgani2(HttpSession session,
									@RequestParam(defaultValue = "0") int page,
									@RequestParam(defaultValue = "10") int size) {
		
		int empno = (int)session.getAttribute("empno");
	    
		ModelAndView model = new ModelAndView();
		
		List<EmpDto> empList = empService.findAll();
	    List<CodeDto> result2 = codeService.list();

	    //부서별 그룹핑
	    Map<String, List<EmpDto>> deptMap = empList.stream()
	        .collect(Collectors.groupingBy(EmpDto::getDept));
	    
	    // 즐겨찾기
	    List<Integer> favList = favorService.findFavoritesByEmpno(empno);
	    
	    if(favList == null) {
	        favList = new ArrayList<>();
	    }
	    
	    //직급별 정리
	    deptMap.values().forEach(list ->
	        list.sort(Comparator.comparing(EmpDto::getPosition))
	    );

	    model.addObject("deptMap", deptMap);
	    model.addObject("list", empList);
	    model.addObject("list2", result2);
	    model.addObject("favList", favList);
	    model.setViewName("/edsm/empList2");

	    return model;
	}
	
	
	// 결재자 즐겨찾기
	@GetMapping("/favorList")
	public ModelAndView favorList(HttpSession session) {
		
		int empno = (int)session.getAttribute("empno");
	    
	    List<EmpDto> empList = empService.findAll();
	    List<CodeDto> codeList = codeService.list();

	    // 내가 즐겨찾기한 사원 번호 리스트
	    List<Integer> favList = favorService.findFavoritesByEmpno(empno);

	    // empList에서 즐겨찾기한 사원만 필터링
	    List<EmpDto> favEmpList = empList.stream()
	        .filter(e -> favList.contains(e.getEmpno()))
	        .toList();

	    // 부서별 그룹핑
	    Map<String, List<EmpDto>> deptMap = favEmpList.stream()
	        .collect(Collectors.groupingBy(EmpDto::getDept));

	    ModelAndView model = new ModelAndView("edsm/favorList"); // 새 fragment 템플릿
	    model.addObject("deptMap", deptMap);
	    model.addObject("list2", codeList);
	    model.addObject("favList", favList);

	    return model;
	}
	
	/**
	 * 기안 저장
	 */
	@PostMapping("/edsmSave")
	@ResponseBody
	@Transactional
	public String submitDocument(EdsmDto dto, HttpSession session,
								@RequestParam(value = "viewerIds", required = false) String viewerIds) {
	    
		int empno = (int) session.getAttribute("empno");

	    dto.setEmpno(empno);
	    dto.setEdst("F60001");     // 결재 진행 중
	    dto.setIsdraft("N");       // 임시보관 아님

	    
	    EdsmDto saved = edsmService.save(dto);

	    // 기안자 결재라인 저장
	    EdsmlineDto drafterLine = EdsmlineDto.builder()
	            .edsmno(saved.getEdsmno())
	            .empno(empno)
	            .edpro(0)
	            .edst("C30000")
	            .build();
	    edsmlineService.save(drafterLine);

	    //결재자 결재라인 저장
	    String[] approvers = dto.getApproverIds().split(",");
	    int order = 1;
	    for (String approver : approvers) {
	        int approverEmpno = Integer.parseInt(approver);

	        // 기안자와 같은 사람은 제외
	        if (approverEmpno == empno) continue;

	        EdsmlineDto line = EdsmlineDto.builder()
	                .edsmno(saved.getEdsmno())
	                .empno(approverEmpno)
	                .edpro(order)
	                .edst("C30001")
	                .build();

	        edsmlineService.save(line);
	        order++;
	    }
	    
		 //  공유자(뷰어) 라인
	    if (StringUtils.hasText(viewerIds)) {
	        String[] viewers = viewerIds.split(",");
	        for (String v : viewers) {
	            int viewerEmpno = Integer.parseInt(v.trim());
	            ViewerDto viewer = ViewerDto.builder()
	                    .edsmno(saved.getEdsmno())
	                    .empno(viewerEmpno)
	                    .build();
	            viewerService.save(viewer);
	        }
	    }

	    

	    return "1";
	}
	
	/**
	 * 기안 임시 저장
	 */
	@PostMapping("/edsmIsdraft")
	@ResponseBody
	public String isdraftDocument(EdsmDto dto, HttpSession session,
									@RequestParam(value = "viewerIds", required = false) String viewerIds) {
		int empno = (int) session.getAttribute("empno");

	    dto.setEmpno(empno);
	    dto.setEdst("F60001");     // 결재 진행 중
	    dto.setIsdraft("Y");       // 임시보관

	    EdsmDto saved = edsmService.save(dto);

	    // 기안자 결재라인 저장
	    EdsmlineDto drafterLine = EdsmlineDto.builder()
	            .edsmno(saved.getEdsmno())
	            .empno(empno)
	            .edpro(0)
	            .edst("C30000") // 기안 상태 코드
	            .build();
	    edsmlineService.save(drafterLine);

	    // 2. 결재자 결재라인 저장 (기안자 제외)
	    String[] approvers = dto.getApproverIds().split(",");
	    int order = 1;
	    for (String approver : approvers) {
	        int approverEmpno = Integer.parseInt(approver);

	        // 기안자 중복 방지
	        if (approverEmpno == empno) continue;

	        EdsmlineDto line = EdsmlineDto.builder()
	                .edsmno(saved.getEdsmno())
	                .empno(approverEmpno)
	                .edpro(order)
	                .edst("C30001") // 대기 상태
	                .build();

	        edsmlineService.save(line);
	        order++;
	    }
	    
	    //  공유자(뷰어) 라인
	    if (StringUtils.hasText(viewerIds)) {
	        String[] viewers = viewerIds.split(",");
	        for (String v : viewers) {
	            int viewerEmpno = Integer.parseInt(v.trim());
	            ViewerDto viewer = ViewerDto.builder()
	                    .edsmno(saved.getEdsmno())
	                    .empno(viewerEmpno)
	                    .build();
	            viewerService.save(viewer);
	        }
	    }	    
	    
	    return "1";
	}
	
	/**
	 * 결재목록
	 */
	@GetMapping
	public ModelAndView edsmMain(	@RequestParam(defaultValue = "0") int page,
									@RequestParam(defaultValue = "10") int size,
									HttpSession session,
									@RequestParam(required = false) String status,
							        @RequestParam(required = false) String searchType,
							        @RequestParam(required = false) String keyword,
							        @RequestParam(required = false) String fromDate,
							        @RequestParam(required = false) String toDate) {
	    
		Integer empno = (Integer)session.getAttribute("empno");
		if (empno == null) {
	        return new ModelAndView("redirect:/login/login");
	    }
		EmpDto emp = empService.findById(empno);
	    if (emp == null || "N".equals(emp.getState())) {
	        session.invalidate();
	        return new ModelAndView("redirect:/login/login");
	    }
		
	    // 페이징 + 상태필터 + 검색(제목/기안자) 호출
	    Pageable pageable = PageRequest.of(page, size, Sort.by("wdate").descending());
	    Page<EdsmDto> pageResult =
	        edsmService.findDocsByEmpno(empno, status, searchType, keyword, pageable);
	    List<EdsmDto> list = pageResult.getContent();
	    DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
	    if (fromDate != null && !fromDate.isBlank()) {
	        LocalDate from = LocalDate.parse(fromDate, fmt);
	        list = list.stream()
	                   .filter(d -> {
	                       LocalDate w = d.getWdate()
	                                      .toLocalDateTime()
	                                      .toLocalDate();
	                       return !w.isBefore(from);  // w >= from
	                   })
	                   .collect(Collectors.toList());
	    }
	    if (toDate != null && !toDate.isBlank()) {
	        LocalDate to = LocalDate.parse(toDate, fmt);
	        list = list.stream()
	                   .filter(d -> {
	                       LocalDate w = d.getWdate()
	                                      .toLocalDateTime()
	                                      .toLocalDate();
	                       return !w.isAfter(to);     // w <= to
	                   })
	                   .collect(Collectors.toList());
	    }
	    
	    
	    if (status != null && !status.isEmpty()) {
	        list = list.stream()
	                   .filter(d -> status.equals(d.getEdst()))
	                   .collect(Collectors.toList());
	    }
	    
	    // 회수 상태 필터링
	    list = list.stream()
	               .filter(d ->
	                   !"F60004".equals(d.getEdst())
	                   || d.getEmpno() == empno
	               )
	               .collect(Collectors.toList());
	    
	    // Collections.reverse(list);
	    
	    
	 // --- 공유자(viewer) 문서 중 '승인 완료(F60002)' 인 것만 추가 ---
	    List<ViewerDto> viewers = viewerService.findByEmpno(empno);
	    for (ViewerDto v : viewers) {
	        boolean already = list.stream()
	                              .anyMatch(d -> d.getEdsmno() == v.getEdsmno());
	        if (!already) {
	            EdsmDto doc = edsmService.findById(v.getEdsmno());
	            if ("F60003".equals(doc.getEdst())) {
	                list.add(doc);
	            }
	        }
	    }
	    // 추가된 문서들도 포함해서, wdate 역순으로 정렬
	    list.sort(Comparator.comparing(EdsmDto::getWdate).reversed());

	    // 스트림으로 edsmno 리스트 만들기
	    List<Integer> edsmnos = list.stream()
	                                 .map(EdsmDto::getEdsmno)
	                                 .collect(Collectors.toList()); // Java 8~11
	    // edsmno 로 결재선 조회
	    List<EdsmlineDto> lines = edsmlineService.findByEdsmnos(edsmnos);
	       
	    List<EmpDto> empList = empService.findAll();
	    List<CodeDto> codeList = codeService.list();
	    
	    ModelAndView model = new ModelAndView("edsm/edsmMain");
	    
	    Map<Integer, String> approverMap = new HashMap<>();

	    for (EdsmDto dto : list) {
	        List<EdsmlineDto> relatedLines = lines.stream()
	            .filter(l -> l.getEdsmno() == dto.getEdsmno())
	            .toList();

	        EdsmlineDto targetLine = null;

	        switch (dto.getEdst()) {
	            case "F60003": // 결재완료 → 승인자 중 가장 마지막 사람
	                targetLine = relatedLines.stream()
	                    .filter(l -> "C30002".equals(l.getEdst()))
	                    .max(Comparator.comparingInt(l -> l.getEdpro()))
	                    .orElse(null);
	                break;
	            case "F60005": // 반려 → 반려한 사람
	                targetLine = relatedLines.stream()
	                    .filter(l -> "C30003".equals(l.getEdst()))
	                    .findFirst()
	                    .orElse(null);
	                break;
	            case "F60001":
	            case "F60002": // 대기중/진행중 → 다음 결재자
	                targetLine = relatedLines.stream()
	                    .filter(l -> "C30001".equals(l.getEdst()))
	                    .min(Comparator.comparingInt(l -> l.getEdpro()))
	                    .orElse(null);
	                break;
	            case "F60004": // 회수 → 기안자
	                String drafter = empList.stream()
	                    .filter(e -> e.getEmpno() == dto.getEmpno())
	                    .map(EmpDto::getName)
	                    .findFirst().orElse("기안자 없음");
	                approverMap.put(dto.getEdsmno(), drafter);
	                continue; // 다음 문서로 이동
	        }

	        if (targetLine != null) {
	            int approverEmpno = targetLine.getEmpno(); // 변수 이름 변경
	            String name = empList.stream()
	                .filter(e -> e.getEmpno() == approverEmpno)
	                .map(EmpDto::getName)
	                .findFirst().orElse("이름없음");
	            approverMap.put(dto.getEdsmno(), name);
	        }
	    }

	    model.addObject("approverMap", approverMap);
	    model.addObject("empList", empList);
	    model.addObject("list", list);     // 문서 리스트
	    model.addObject("codeList", codeList);
	    model.addObject("lines", lines);   // 결재선 전체
	    model.addObject("selectedStatus", status);   // 결재선 전체
	    model.addObject("page", pageResult);
	    model.addObject("searchType", searchType);
	    model.addObject("keyword", keyword);
	    model.addObject("fromDate",fromDate);
	    model.addObject("toDate",toDate);
	    return model;
	}
	
	/**
	 * 결제 상세페이지
	 */
	@GetMapping("/edsmDetail/{edsmno}")
	public ModelAndView viewDetail(@PathVariable int edsmno, HttpSession session) {
		
		Integer empno = (Integer)session.getAttribute("empno");
		if (empno == null) {
	        return new ModelAndView("redirect:/login/login");
	    }
		System.out.println("현재 로그인 empno: " + empno);
		EmpDto emp = empService.findById(empno);
	    if (emp == null || "N".equals(emp.getState())) {
	        session.invalidate();
	        return new ModelAndView("redirect:/login/login");
	    }

	    EdsmDto edsm = edsmService.findById(edsmno);
	    List<EdsmlineDto> lines = edsmlineService.findByEdsmno(edsmno);
	    List<EmpDto> empList = empService.findAll();
	    List<CodeDto> codeList = codeService.list();
	    List<ViewerDto> viewerList = viewerService.findByEdsmno(edsmno);

	    Map<Integer, String> empNameMap = empList.stream()
	        .collect(Collectors.toMap(EmpDto::getEmpno, EmpDto::getName));

	    //다음결재 순서
	    int nextPro = lines.stream()
	        .filter(l -> "C30001".equals(l.getEdst()))        // 승인 대기 상태
	        .mapToInt(EdsmlineDto::getEdpro)                   // 순서 번호
	        .min()                                            // 최소 순서(=현재 차례)
	        .orElse(-1);

	    boolean isCurrentApprover = nextPro != -1 && lines.stream()
	        .anyMatch(l ->
	            "C30001".equals(l.getEdst()) &&             // 대기 상태
	            l.getEdpro() == nextPro &&                  // 현재 차례
	            l.getEmpno() == empno                       // 로그인 사원
	        );

	    
	    ModelAndView model = new ModelAndView("edsm/edsmDetail");
	    model.addObject("viewerList", viewerList);
	    model.addObject("edsm", edsm);
	    model.addObject("lines", lines);
	    model.addObject("empList", empList);
	    model.addObject("codeList", codeList);
	    model.addObject("empNameMap", empNameMap);
	    model.addObject("loginEmpno", empno);
	    model.addObject("isCurrentApprover", isCurrentApprover);
	    System.out.println("edsm.edst: " + edsm.getEdst());
	    return model;
	}
	

	/**
	 * 임시보관함 목록
	 */
	@GetMapping("/edsmDraft")
	public ModelAndView edsmDraftList(HttpSession session,
										@RequestParam(required = false) String keyword) {
		
		Integer empno = (Integer)session.getAttribute("empno");
		if (empno == null) {
	        return new ModelAndView("redirect:/login/login");
	    }
		EmpDto emp = empService.findById(empno);
	    if (emp == null || "N".equals(emp.getState())) {
	        session.invalidate();
	        return new ModelAndView("redirect:/login/login");
	    }

	    List<EdsmDto> list = edsmService.findDraftsByEmpno(empno);
		if (keyword != null && !keyword.isBlank()) {
			String lower = keyword.toLowerCase();
			list = list.stream()
					.filter(d -> d.getEdtitle() != null
					&& d.getEdtitle().toLowerCase().contains(lower))
					.collect(Collectors.toList());
		}
	    List<Integer> edsmnos = list.stream().map(EdsmDto::getEdsmno).toList();
	    List<EdsmlineDto> lines = edsmlineService.findByEdsmnos(edsmnos);
	    List<EmpDto> empList = empService.findAll();
	    List<CodeDto> codeList = codeService.list();

	    ModelAndView model = new ModelAndView("edsm/edsmDraft");

	    Map<Integer, String> approverMap = new HashMap<>();

	    for (EdsmDto dto : list) {
	        List<EdsmlineDto> relatedLines = lines.stream()
	            .filter(l -> l.getEdsmno() == dto.getEdsmno())
	            .toList();

	        // 기안자 이름을 approverMap에 넣기 (임시보관은 결재 진행 전 상태)
	        String drafter = empList.stream()
	            .filter(e -> e.getEmpno() == dto.getEmpno())
	            .map(EmpDto::getName)
	            .findFirst().orElse("기안자 없음");
	        approverMap.put(dto.getEdsmno(), drafter);
	    }
	    
	    Map<Integer, String> empNameMap = empList.stream()
	    	    .collect(Collectors.toMap(EmpDto::getEmpno, EmpDto::getName));

	    model.addObject("empNameMap", empNameMap);
	    model.addObject("approverMap", approverMap);
	    model.addObject("empList", empList);
	    model.addObject("list", list);     // 임시보관 문서 리스트
	    model.addObject("codeList", codeList);
	    model.addObject("lines", lines);   // 결재선
	    model.addObject("keyword", keyword);

	    return model;
	}
	
	
	/**
	 * 임시보관함 목록에서 삭제
	 */	
	@PostMapping("/deleteDraft/{edsmno}")
	@ResponseBody
	@Transactional
	public ResponseEntity<String> deleteDraft(@PathVariable int edsmno) {
		// 연관 결재선 삭제
	    edsmlineService.deleteByEdsmno(edsmno);
	    // 문서 삭제
	    edsmService.deleteDraft(edsmno);
	    // 공유자 삭제
	    viewerService.deleteByEdsmno(edsmno);
	    return ResponseEntity.ok("삭제 완료");
	}
	
	
	/**
	 * 임시보관함 상세보기
	 */	
	@GetMapping("/edsmDraftDetail/{edsmno}")
	public ModelAndView viewDraftDetail(@PathVariable int edsmno,HttpSession session) {
		Integer empno = (Integer)session.getAttribute("empno");
		if (empno == null) {
	        return new ModelAndView("redirect:/login/login");
	    }
		EmpDto emp1 = empService.findById(empno);
	    if (emp1 == null || "N".equals(emp1.getState())) {
	        session.invalidate();
	        return new ModelAndView("redirect:/login/login");
	    }
	    
	    ModelAndView model = new ModelAndView("edsm/edsmDraftDetail");
	    
	    EdsmDto edsm = edsmService.findById(edsmno);
	    List<EdsmlineDto> lines = edsmlineService.findByEdsmno(edsmno);
	    List<EmpDto> empList = empService.findAll();
	    List<CodeDto> codeList = codeService.list();
	    List<ViewerDto> viewerList = viewerService.findByEdsmno(edsmno);
	    
	    Map<Integer, String> empNameMap = empList.stream()
	        .collect(Collectors.toMap(EmpDto::getEmpno, EmpDto::getName));

	    // 그룹별 정리 모달용
	    Map<String, List<EmpDto>> deptMap = empList.stream()
	        .collect(Collectors.groupingBy(EmpDto::getDept));
	    // 포지션별 정렬
	    deptMap.values().forEach(list ->
	        list.sort(Comparator.comparing(EmpDto::getPosition))
	    );
	    
	  //즐겨찾기
	    List<Integer> favList = Optional.ofNullable(favorService.findFavoritesByEmpno(empno))
	    	    .orElseGet(ArrayList::new);

    	List<EmpDto> favEmpList = empList.stream()
    	    .filter(emp -> favList.contains(Integer.valueOf(emp.getEmpno())))
    	    .collect(Collectors.toList());
	   
	    model.addObject("edsm", edsm);
	    model.addObject("lines", lines);
	    model.addObject("empNameMap", empNameMap);
	    model.addObject("codeList", codeList);
	    model.addObject("loginEmpno", empno);
	    
	    model.addObject("viewerList", viewerList);
	    model.addObject("list1", empList);
	    // 모달
	    model.addObject("deptMap", deptMap);
	    model.addObject("list2", codeList);
	    // 즐겨찾기
	    model.addObject("favEmpList", favEmpList);
	    model.addObject("favList", favList);
	    return model;
	}
	
	/**
	 * 결재 문서 승인
	 */	
	@PostMapping("/approve")
	public void submitApproval(@RequestParam int edsmno,
								@RequestParam String action,
								@RequestParam(required=false) String comment,
								HttpSession session,
								HttpServletResponse response) throws IOException {
		
		int empno = (int) session.getAttribute("empno");
		
		List<EdsmlineDto> lines = edsmlineService.findByEdsmno(edsmno);
		
        lines.stream()
             .filter(l -> l.getEmpno() == empno
                       && "C30001".equals(l.getEdst()))
             .findFirst()
             .ifPresent(l -> {
                 // approve → C30002, reject → C30003
                 l.setEdst("approve".equals(action) ? "C30002" : "C30003");
                 edsmlineService.save(l);
             });

        EdsmDto edsm = edsmService.findById(edsmno);
        if ("reject".equals(action)) {
            edsm.setEdst("F60005");       // 반려 상태
            edsm.setEdcomment(comment);
            edsm.setApdate(new java.sql.Timestamp(System.currentTimeMillis()));
        } else {
            // 다음 승인 대기자가 남아 있으면 진행중, 없으면 최종 완료
            boolean hasMore = lines.stream()
                                   .anyMatch(l -> "C30001".equals(l.getEdst()));
            if (hasMore) {
                edsm.setEdst("F60002");   // 진행중
            } else {
                edsm.setEdst("F60003");   // 결재완료
                edsm.setApdate(new java.sql.Timestamp(System.currentTimeMillis()));
            }
            
        }
        edsmService.save(edsm);

	    //목록으로 리다이렉트
	    response.sendRedirect("/edsm");
	}
	
	/**
     * 회수 처리
     */
    @PostMapping("/retrieve")
    public void submitRetrieve(@RequestParam int edsmno,
						            HttpSession session, 
						            HttpServletResponse response) throws IOException {

    	int empno = (int) session.getAttribute("empno");
        
        EdsmDto edsm = edsmService.findById(edsmno);
        if (edsm.getEmpno() == empno) {
            edsm.setEdst("F60004");  // F60004: 회수
            edsm.setApdate(new java.sql.Timestamp(System.currentTimeMillis()));
            edsmService.save(edsm);
        }

        response.sendRedirect("/edsm");
    }
	
}
