// MailController.java
package com.groupware.controller;

import com.groupware.dto.MailListDto;
import com.groupware.dto.UserDto;
import com.groupware.entity.BlockDto;
import com.groupware.entity.CodeDto;
import com.groupware.entity.EmpDto;
import com.groupware.entity.MailDto;
import com.groupware.repository.BlockRepository;
import com.groupware.repository.CodeRepository;
import com.groupware.repository.EmpRepository;
import com.groupware.repository.MailRepository;
import com.groupware.repository.TeamMailRepository;
import com.groupware.service.MailService;
import com.groupware.service.TeamMailService;
import com.groupware.service.UserService;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;



@Controller  
@RequestMapping("/mail")
public class MailController {

	@Autowired
    private BlockRepository blockRepository;
	
    @Autowired
    private EmpRepository empRepository;
    
    @Autowired
    private MailRepository mailRepository;
    
    @Autowired
    private CodeRepository codeRepository;
    
    @Autowired
    private TeamMailRepository teammailRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TeamMailService teamMailService;

    
    private final MailService mailService;
    

    public MailController(MailService mailService,MailRepository mailRepository,UserService userService) {
    	this.mailService = mailService;
    	this.mailRepository = mailRepository;
    	this.userService = userService;
    	
    }
    

    //전체메일함
    @GetMapping
    public ModelAndView maillist(
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "") String searchType,
        @RequestParam(defaultValue = "1") int indexpage,
        HttpSession session
    ) {
        ModelAndView model = new ModelAndView();

        // 세션에서 로그인된 사용자 아이디 꺼내기 (실제 배포용)

        String userId = (String) session.getAttribute("userid");
        if (userId == null) {
            return new ModelAndView("redirect:/login/login");
        }
        String name = (String) session.getAttribute("name");

        
        //String userId = "test1";  // 테스트용 하드코딩
        //String name = "테스트";

        List<String> deptEmails = teamMailService.findTeamMailsByUserId(userId);  // 유저가 속한 부서 대표메일 리스트

        Page<MailDto> mailPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            switch (searchType) {
                case "sendername":
                    mailPage = mailService.searchBySenderName(userId, deptEmails, keyword, indexpage - 1, 10);
                    break;
                case "receivername":
                    mailPage = mailService.searchByReceiverName(userId, deptEmails, keyword, indexpage - 1, 10);
                    break;
                case "subject":
                    mailPage = mailService.searchBySubject(userId, deptEmails, keyword, indexpage - 1, 10);
                    break;
                case "content":
                    mailPage = mailService.searchByContent(userId, deptEmails, keyword, indexpage - 1, 10);
                    break;
                case "date":
                    mailPage = mailService.searchByDate(userId, deptEmails, keyword, indexpage - 1, 10);
                    break;
                default:
                    mailPage = mailService.search(userId, deptEmails, keyword, indexpage - 1, 10);
            }
        } else {
            mailPage = mailService.listWithName(userId, deptEmails, indexpage - 1, 10);
        }

        // mailPage -> mailListDto 변환
        Page<MailListDto> page = mailService.convertToMailListDto(mailPage, userId);

        int startPageRownum = (int) (page.getTotalElements() - page.getNumber() * 10);
        int pageBlockSize = 10;
        int startBlockPage = ((indexpage - 1) / pageBlockSize) * pageBlockSize + 1;
        int endBlockPage = Math.min(startBlockPage + pageBlockSize - 1, page.getTotalPages());

        model.addObject("startBlockPage", startBlockPage);
        model.addObject("endBlockPage", endBlockPage);
        model.addObject("plist", page.getContent());
        model.addObject("startPageRownum", startPageRownum);
        model.addObject("ptotal", page.getTotalElements());
        model.addObject("ptotalPage", page.getTotalPages());
        model.addObject("currentPage", indexpage);
        model.addObject("keyword", keyword);
        model.addObject("searchType", searchType);
        model.addObject("userId", userId);
        model.addObject("name", name);

        model.setViewName("mail/maillist");
        return model;
    }

    
    
    // 메일 디테일 
    @GetMapping("/maildetail/{mailno}")
    public ModelAndView mailDetail(@PathVariable Long mailno, HttpSession session) {
        ModelAndView model = new ModelAndView();

        String userId = (String) session.getAttribute("userid");
        if (userId == null) {
            return new ModelAndView("redirect:/login/login");
        }

        //String userId = "test1"; // 테스트용 하드코딩

        try {
            MailDto mail = mailService.findByIdAndUserId(mailno, userId);
            model.addObject("mail", mail);

            EmpDto senderEmp = empRepository.findByUserid(mail.getSenderId()).orElse(null);
            EmpDto receiverEmp = empRepository.findByUserid(mail.getReceiverId()).orElse(null);

            String senderDeptName = "-";
            if (senderEmp != null && senderEmp.getDept() != null) {
                CodeDto code = codeRepository.findById(senderEmp.getDept()).orElse(null);
                if (code != null && "Y".equals(code.getState()) && code.getNcode() != null) {
                    senderDeptName = code.getNcode();
                }
            }

            String receiverDeptName = "-";
            if (receiverEmp != null && receiverEmp.getDept() != null) {
                CodeDto code = codeRepository.findById(receiverEmp.getDept()).orElse(null);
                if (code != null && "Y".equals(code.getState()) && code.getNcode() != null) {
                    receiverDeptName = code.getNcode();
                }
            }

            model.addObject("senderEmp", senderEmp);
            model.addObject("senderDeptName", senderDeptName);
            model.addObject("receiverEmp", receiverEmp);
            model.addObject("receiverDeptName", receiverDeptName);

            String formattedSentAt = mail.getSentAt() != null
                    ? mail.getSentAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    : "시간 정보 없음";
            model.addObject("formattedSentAt", formattedSentAt);

            // 내가 보낸 메일인지 받은 메일인지 구분하는 기준 예시 (mail.getSenderId() == userId가 내 발신인지)
            boolean isSentMail = userId.equals(mail.getSenderId());
            model.addObject("isSentMail", isSentMail);

            boolean isBlocked;
            if (isSentMail) {
                isBlocked = blockRepository.existsByBlockerIdAndBlockedId(userId, mail.getReceiverId());
            } else {
                isBlocked = blockRepository.existsByBlockerIdAndBlockedId(userId, mail.getSenderId());
            }
            model.addObject("isBlocked", isBlocked);

            model.setViewName("mail/maildetail");
        } catch (RuntimeException e) {
            model.addObject("errorMessage", e.getMessage());
            model.setViewName("error/errorPage");
        }

        return model;
    }


    //전체 메일 단일삭제 (휴지통 이동)
    @PostMapping("/delete")
    public String deleteMail(@RequestParam("mailno") Long mailno, HttpSession session) {
        
    	String userId = (String) session.getAttribute("userid");
        // String userId = "test1"; //하드코딩 테스트용

        // 현재 로그인 사용자가 발신자인지 수신자인지 확인 후 soft delete
        mailService.softDeleteByUser(mailno, userId);

        return "redirect:/mail"; // 삭제 후 전체메일 목록으로 이동
    }

    //전체메일 다중삭제 (휴지통 이동)
    @PostMapping("/deleteSelected")
    @ResponseBody
    public String deleteSelected(@RequestParam("mailIds") List<Long> mailIds, HttpSession session) {
    	String userId = (String) session.getAttribute("userid");
        //String userId = "test1"; //하드코딩 테스트용

        mailService.deleteMailsByIds(mailIds, userId);
        return "ok";
    }
    
    //전체메일 전체삭제 (휴지통 이동)
    @PostMapping("/deleteAllToTrash")
    public String deleteAllToTrash(HttpSession session) {

    	String userId = (String) session.getAttribute("userid");
    	
        //String userId = "test1"; 하드코딩 테스트용

        mailService.moveAllMailsToTrash(userId);
        return "redirect:/mail";
    }

    
    //수신메일함
    @GetMapping("/mailin")
    public ModelAndView receivedList(
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "") String searchType,
        @RequestParam(defaultValue = "1") int indexpage,
        HttpSession session
    ) {
        ModelAndView model = new ModelAndView();

        
        // 세션에서 로그인된 사용자 아이디 꺼내기
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
        if (userId == null) {
            return new ModelAndView("redirect:/login/login");
        }
         

        
        //String userId = "test1";  하드코딩 테스트용
        //String name = "테스트"; 하드코딩 테스트용

        Page<MailListDto> page;
        

        if (keyword != null && !keyword.trim().isEmpty()) {
            switch (searchType) {
                
	            case "sendername":
	                page = mailService.searchReceivedBySenderName(userId, keyword, indexpage - 1, 10);
	                break;
                case "subject":
                    page = mailService.searchReceivedBySubject(userId, keyword, indexpage - 1, 10);
                    break;
                case "content":
                    page = mailService.searchReceivedByContent(userId, keyword, indexpage - 1, 10);
                    break;
                case "date":
                    page = mailService.searchReceivedByDate(userId, keyword, indexpage - 1, 10);
                    break;
                default:
                    page = mailService.searchReceived(userId, keyword, indexpage - 1, 10);
            }
        } else {
        	page = mailService.receivedListWithName(userId, indexpage - 1, 10);

        }

        int startPageRownum = (int) (page.getTotalElements() - page.getNumber() * 10);
        int pageBlockSize = 10;
        int startBlockPage = ((indexpage - 1) / pageBlockSize) * pageBlockSize + 1;
        int endBlockPage = Math.min(startBlockPage + pageBlockSize - 1, page.getTotalPages());

        model.addObject("startBlockPage", startBlockPage);
        model.addObject("endBlockPage", endBlockPage);
        model.addObject("plist", page.getContent());
        model.addObject("startPageRownum", startPageRownum);
        model.addObject("ptotal", page.getTotalElements());
        model.addObject("ptotalPage", page.getTotalPages());
        model.addObject("currentPage", indexpage);
        model.addObject("keyword", keyword);
        model.addObject("searchType", searchType);
        model.addObject("userId", userId);
        model.addObject("name", name);

        model.setViewName("mail/mailin");
        return model;
    }
    
    
    // 수신메일 다중삭제 (휴지통 이동)
    @PostMapping("/deleteSelectedReceived")
    @ResponseBody
    public String deleteSelectedReceived(@RequestParam("mailIds") List<Long> mailIds, HttpSession session) {
        
    	String userId = (String) session.getAttribute("userid");
        // String userId = "test1"; //하드코딩 테스트용

        mailService.deleteReceivedMailsByIds(userId, mailIds);
        return "ok";
    }

    
    //수신메일 전체삭제 (휴지통 이동)
    @PostMapping("/deleteAllReceived")
    @ResponseBody
    public ResponseEntity<String> deleteAllReceived(HttpSession session) {
         
    	String userId = (String) session.getAttribute("userid");
    	// String userId = "test1"; // 하드코딩 테스트용
        try {
            mailService.moveAllReceivedMailsToTrash(userId);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
    }

    
	// 발신메일함
    @GetMapping("/mailout")
    public ModelAndView sendList(
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "") String searchType,
        @RequestParam(defaultValue = "1") int indexpage,
        HttpSession session
    ) {
        ModelAndView model = new ModelAndView();

        // 세션에서 로그인된 사용자 아이디 꺼내기
       
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
        if (userId == null) {
            return new ModelAndView("redirect:/login/login");
        }
        
        // String userId = "test1"; 하드코딩 테스트용
        // String name = "테스트";  하드코딩 테스트용 

        Page<MailListDto> page;

        if (keyword != null && !keyword.trim().isEmpty()) {
            switch (searchType) {
                case "receivername":
                    page = mailService.searchSendByReceiverName(userId, keyword, indexpage - 1, 10);
                    break;
                case "subject":
                    page = mailService.searchSendBySubject(userId, keyword, indexpage - 1, 10);
                    break;
                case "content":
                    page = mailService.searchSendByContent(userId, keyword, indexpage - 1, 10);
                    break;
                case "date":
                    page = mailService.searchSendByDate(userId, keyword, indexpage - 1, 10);
                    break;
                default:
                    page = mailService.searchSend(userId, keyword, indexpage - 1, 10);
            }
        } else {
            page = mailService.sendListWithName(userId, indexpage - 1, 10);
        }

        int startPageRownum = (int) (page.getTotalElements() - page.getNumber() * 10);
        int pageBlockSize = 10;
        int startBlockPage = ((indexpage - 1) / pageBlockSize) * pageBlockSize + 1;
        int endBlockPage = Math.min(startBlockPage + pageBlockSize - 1, page.getTotalPages());

        model.addObject("startBlockPage", startBlockPage);
        model.addObject("endBlockPage", endBlockPage);
        model.addObject("plist", page.getContent());
        model.addObject("startPageRownum", startPageRownum);
        model.addObject("ptotal", page.getTotalElements());
        model.addObject("ptotalPage", page.getTotalPages());
        model.addObject("currentPage", indexpage);
        model.addObject("startBlockPage", startBlockPage);
        model.addObject("endBlockPage", endBlockPage);
        model.addObject("keyword", keyword);
        model.addObject("searchType", searchType);
        model.addObject("userId", userId);
        model.addObject("name", name);

        model.setViewName("mail/mailout");
        return model;
    }

    
   //발신메일 다중삭제 (휴지통 이동)
    @PostMapping("/deleteSelectedSent")
    @ResponseBody
    public String deleteSelectedSent(@RequestParam("mailIds") List<Long> mailIds, HttpSession session) {
        // 세션에서 로그인된 사용자 아이디 꺼내기
      
    	String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
        //String userId = "test1"; // 하드코딩 테스트용

        mailService.deleteSentMailsByIds(userId, mailIds);
        return "ok";
    }

    //발신메일 전체삭제 (휴지통 이동)
    @PostMapping("/deleteAllSent")
    @ResponseBody
    public ResponseEntity<String> deleteAllSent(HttpSession session) {
     
        String userId = (String) session.getAttribute("userid");
    	
    	//String userId = "test1"; // 하드코딩 테스트용
        try {
            mailService.moveAllSentMailsToTrash(userId);
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
    }
    

    // 메일 휴지통 
    @GetMapping("/mailtrash")
    public ModelAndView trashList(
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "") String searchType,
        @RequestParam(defaultValue = "1") int indexpage,
        HttpSession session
    ) {
        ModelAndView model = new ModelAndView();

      
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
        if (userId == null) {
            return new ModelAndView("redirect:/login/login");
        }
        //String userId = "test1";  // 하드코딩 테스트용
        //String name = "테스트"; 하드코딩 테스트용

        Page<MailListDto> page;

        if (keyword != null && !keyword.trim().isEmpty()) {
            switch (searchType) {
                case "sendername":
                    page = mailService.searchTrashBySenderName(userId, keyword, indexpage - 1, 10);
                    break;
                case "receivername":
                    page = mailService.searchTrashByReceiverName(userId, keyword, indexpage - 1, 10);
                    break;
                case "subject":
                    page = mailService.searchTrashBySubject(userId, keyword, indexpage - 1, 10);
                    break;
                case "content":
                    page = mailService.searchTrashByContent(userId, keyword, indexpage - 1, 10);
                    break;
                case "date":
                    page = mailService.searchTrashByDate(userId, keyword, indexpage - 1, 10);
                    break;
                default:
                    page = mailService.trashList(userId, indexpage - 1, 10);
            }
        } else {
            page = mailService.trashList(userId, indexpage - 1, 10);
        }

        int startPageRownum = (int) (page.getTotalElements() - page.getNumber() * 10);
        int pageBlockSize = 10;
        int startBlockPage = ((indexpage - 1) / pageBlockSize) * pageBlockSize + 1;
        int endBlockPage = Math.min(startBlockPage + pageBlockSize - 1, page.getTotalPages());

        model.addObject("startBlockPage", startBlockPage);
        model.addObject("endBlockPage", endBlockPage);
        model.addObject("plist", page.getContent());
        model.addObject("startPageRownum", startPageRownum);
        model.addObject("ptotal", page.getTotalElements());
        model.addObject("ptotalPage", page.getTotalPages());
        model.addObject("currentPage", indexpage);
        model.addObject("keyword", keyword);
        model.addObject("searchType", searchType);
        model.addObject("userId", userId);
        model.addObject("name", name);

        model.setViewName("mail/mailtrash");
        return model;
    }
    
    // 휴지통 선택복구
    @PostMapping("/trash/restoreSelected")
    @ResponseBody
    public String restoreSelected(@RequestParam("mailIds") List<Long> mailIds,HttpSession session) {
    	
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
    	//String userId ="test1"; // 하드코딩 테스트용
    	//String name ="테스트"; // 하드코딩 테스트용
    	mailService.restoreMails(mailIds, userId);
        return "ok";
    }
    
    // 휴지통 선택삭제
    @PostMapping("/trash/deleteSelected")
    @ResponseBody
    public String deleteSelectedFromTrash(@RequestParam List<Long> mailIds,HttpSession session) {
    	
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
    	//String userId ="test1"; // 하드코딩 테스트용
    	//String name ="테스트"; // 하드코딩 테스트용
    	mailService.deleteMails(mailIds, userId);
        return "ok";
    }
    
    

    // 휴지통 전체 복구 (검색 조건 포함, 페이징 없이 전체)
    @PostMapping("/trash/restoreAll")
    public String restoreAll(
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "") String searchType,
        HttpSession session
    ) {
       
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
    	//String userId ="test1"; // 하드코딩 테스트용
    	//String name ="테스트"; // 하드코딩 테스트용

        mailService.restoreAll(userId, keyword, searchType);
        return "redirect:/mail/mailtrash?keyword=" + keyword + "&searchType=" + searchType;
    }

    // 휴지통 전체 삭제 (검색 조건 포함, 페이징 없이 전체)
    @PostMapping("/trash/deleteAllPermanent")
    public String deleteAllPermanent(
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "") String searchType,
        HttpSession session
    ) {
       
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
        //String userId ="test1"; // 하드코딩 테스트용
    	//String name ="테스트"; // 하드코딩 테스트용

        mailService.deleteAll(userId, keyword, searchType);
        return "redirect:/mail/mailtrash?keyword=" + keyword + "&searchType=" + searchType;
    }


    // 메일 작성 폼
    @GetMapping("/mailwrite")
    public ModelAndView mailWriteForm(HttpSession session) {
        // 실제 배포용 (세션 로그인 체크)
       
       
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
        if (userId == null) {
            return new ModelAndView("redirect:/login/login");
        }

        
    	//String userId ="test1"; // 하드코딩 테스트용
    	//String name ="테스트"; //하드코딩 테스트용

        MailDto draft = new MailDto();
        draft.setSenderId(userId); // 혹시 몰라 미리 세팅

        ModelAndView mv = new ModelAndView("mail/mailwrite");
        mv.addObject("draft", draft);
        mv.addObject("replyMode", false); 
        return mv;
    }
 // 메일 전송
    @PostMapping("/send")
    public String sendMail(@ModelAttribute MailDto mailDto,
                           @RequestParam("file") MultipartFile file, // 첨부파일 받기
                           HttpSession session,
                           @RequestParam(required = false) Boolean sendToSelf) throws IOException {

         String userId = (String) session.getAttribute("userid");
        //String userId = "test1";  // 하드코딩 테스트용
        String senderEmail = userId + "@example.com"; 

        mailDto.setSenderId(userId);
        mailDto.setMailSender(senderEmail);

        mailDto.setMailDraft("N");
        mailDto.setSentAt(LocalDateTime.now());
       // 파일첨부 
       if (file != null && !file.isEmpty()) {
            String uploadDir = new ClassPathResource("static/upload").getFile().getAbsolutePath();

            String originalFilename = file.getOriginalFilename();
            String savedFilename = UUID.randomUUID() + "_" + originalFilename;
            Path filePath = Paths.get(uploadDir, savedFilename);

            Files.copy(file.getInputStream(), filePath);

            mailDto.setMailAttachment("Y");
            mailDto.setAttachmentName(savedFilename);
        } else {
            mailDto.setMailAttachment("N");
            mailDto.setAttachmentName(null);
        }

        if (Boolean.TRUE.equals(sendToSelf)) {
            // 내게 보내기
            mailDto.setReceiverId(userId);
            mailDto.setMailReceiver(senderEmail);

            if (mailDto.getMailno() != null) {
                mailService.sendFromDraft(mailDto); // 임시저장된 메일 업데이트 후 전송
            } else {
                mailService.sendMail(mailDto); // 새 메일 전송
            }

        } else {
            String receiverInput = mailDto.getReceiverId();
            if (receiverInput == null || receiverInput.trim().isEmpty()) {
                throw new IllegalArgumentException("받는 사람 이메일이 비어 있습니다.");
            }

            String[] receivers = receiverInput.split(",");

            for (String raw : receivers) {
                String token = raw.trim();
                if (token.isEmpty()) continue;

                if (token.startsWith("@dept:")) {
                    String deptCode = token.substring(6);

                    List<EmpDto> empList = empRepository.findByDept(deptCode);
                    if (empList == null || empList.isEmpty()) {
                        continue;
                    }

                    for (EmpDto emp : empList) {
                        MailDto copy = new MailDto();
                        copy.setSenderId(userId);
                        copy.setMailSender(senderEmail);
                        copy.setReceiverId(emp.getUserid());
                        copy.setMailReceiver(emp.getUserid() + "@example.com");
                        copy.setSubject(mailDto.getSubject());
                        copy.setContent(mailDto.getContent());
                        copy.setMailDraft("N");
                        copy.setSentAt(LocalDateTime.now());

                        // 첨부파일 정보 복사 추가
                        copy.setMailAttachment(mailDto.getMailAttachment());
                        copy.setAttachmentName(mailDto.getAttachmentName());

                        mailService.sendMail(copy);
                    }

                } else {
                    String email = token.contains("@") ? token : token + "@example.com";
                    String receiverId = email.substring(0, email.indexOf("@"));

                    if (mailDto.getMailno() != null && receivers.length == 1) {
                        // 임시저장 메일로 단일 수신자일 때만 업데이트 & 전송
                        mailDto.setReceiverId(receiverId);
                        mailDto.setMailReceiver(email);
                        mailService.sendFromDraft(mailDto);

                    } else {
                        // 새 메일 복사본 생성 후 전송 (임시저장 메일이거나 다중수신자 모두 여기로 처리)
                        MailDto copy = new MailDto();
                        copy.setSenderId(userId);
                        copy.setMailSender(senderEmail);
                        copy.setReceiverId(receiverId);
                        copy.setMailReceiver(email);
                        copy.setSubject(mailDto.getSubject());
                        copy.setContent(mailDto.getContent());
                        copy.setMailDraft("N");
                        copy.setSentAt(LocalDateTime.now());

                        // 첨부파일 정보 복사 추가
                        copy.setMailAttachment(mailDto.getMailAttachment());
                        copy.setAttachmentName(mailDto.getAttachmentName());

                        mailService.sendMail(copy);
                    }
                }
            }
        }

        return "redirect:/mail";
    }

	// 내게 쓴 메일함
    @GetMapping("/mailtome")
    public ModelAndView myList(
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "") String searchType,
        @RequestParam(defaultValue = "1") int indexpage,
        HttpSession session
    ) {
        ModelAndView model = new ModelAndView();

        // 실제 세션에서 로그인한 사용자 아이디 받아야 함
       
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
        if (userId == null) {
            return new ModelAndView("redirect:/login/login");
        }
        
        //String userId ="test1"; // 하드코딩 테스트용
    	//String name ="테스트"; // 하드코딩 테스트용


        Page<MailListDto> page;

        if (keyword != null && !keyword.trim().isEmpty()) {
            switch (searchType) {               
                case "subject":
                    page = mailService.searchMailToMeBySubject(userId, keyword, indexpage - 1, 10);
                    break;
                case "content":
                    page = mailService.searchMailToMeByContent(userId, keyword, indexpage - 1, 10);
                    break;
                case "date":
                    page = mailService.searchMailToMeByDate(userId, keyword, indexpage - 1, 10);
                    break;
                default:
                    page = mailService.searchMailToMe(userId, keyword, indexpage - 1, 10);
            }
        } else {
            page = mailService.mailToMeListWithName(userId, indexpage - 1, 10);
        }

        int startPageRownum = (int) (page.getTotalElements() - page.getNumber() * 10);
        int pageBlockSize = 10;
        int startBlockPage = ((indexpage - 1) / pageBlockSize) * pageBlockSize + 1;
        int endBlockPage = Math.min(startBlockPage + pageBlockSize - 1, page.getTotalPages());

        model.addObject("startBlockPage", startBlockPage);
        model.addObject("endBlockPage", endBlockPage);
        model.addObject("plist", page.getContent());
        model.addObject("startPageRownum", startPageRownum);
        model.addObject("ptotal", page.getTotalElements());
        model.addObject("ptotalPage", page.getTotalPages());
        model.addObject("currentPage", indexpage);
        model.addObject("keyword", keyword);
        model.addObject("searchType", searchType);
        model.addObject("userId", userId);
        model.addObject("name", name);

        model.setViewName("mail/mailtome");
        return model;
    }


    
    
    // 내게 쓴 메일함 전체삭제 (휴지통 이동)
    @PostMapping("/mailtome/deleteAllToTrash")
    public String deleteAllMailToMeToTrash(HttpSession session) {
       
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");

        //String userId ="test1"; // 하드코딩 테스트용
    	//String name ="테스트"; // 하드코딩 테스트용
        mailService.deleteAllMailToMe(userId);
        return "redirect:/mail/mailtome";
    }


    // 메일 임시보관
    @PostMapping("/draft")
    public ResponseEntity<?> saveDraft(@RequestBody MailDto dto, HttpSession session) {
        // 실제 배포 시 주석 해제
    	String userId = (String) session.getAttribute("userid");
        String userEmail = (String) session.getAttribute("email");

        // 테스트용 하드코딩
        //String userId = "test1"; 하드코딩 테스트용
        //String userEmail = "test1@example.com";하드코딩 테스트용

        // 보내는 사람 정보 세팅
        dto.setSenderId(userId);
        dto.setMailSender(userEmail);

        // 임시저장 표시 (중요!!)
        dto.setMailDraft("Y");
        dto.setSentAt(null);  // 발송시간 초기화, 아직 안 보낸 상태

        // 받는 사람 정보 보정 및 세팅
        if (dto.getReceiverId() == null || dto.getReceiverId().trim().isEmpty()) {
            if (dto.getMailReceiver() != null && !dto.getMailReceiver().trim().isEmpty()) {
                String mailReceiver = dto.getMailReceiver().trim();
                if (mailReceiver.contains("@")) {
                    // 이메일 형식이면 receiverId는 @ 앞 부분만
                    String userIdPart = mailReceiver.split("@")[0];
                    dto.setReceiverId(userIdPart);
                    dto.setMailReceiver(mailReceiver);  // 이메일 그대로 유지
                } else {
                    // 이메일 형식 아니면 @example.com 붙여서 보정
                    dto.setReceiverId(mailReceiver);
                    dto.setMailReceiver(mailReceiver + "@example.com");
                }
            } else {
                // 둘 다 없으면 기본값 세팅 (에러 방지용)
                dto.setReceiverId("unknown");
                dto.setMailReceiver("unknown@example.com");
            }
        } else {
            String receiver = dto.getReceiverId().trim();
            if (!receiver.contains("@")) {
                // 내부 사용자 ID로 간주, @example.com 붙임
                dto.setReceiverId(receiver);
                dto.setMailReceiver(receiver + "@example.com");
            } else {
                // 이메일 형식이면
                dto.setReceiverId(receiver.split("@")[0]);
                dto.setMailReceiver(receiver);
            }
        }

        try {
            mailService.saveDraft(dto, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    
     
    // 임시보관함 메일 리스트
    @GetMapping("/maildrafts")
    public ModelAndView getDraftMails(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String searchType,
            @RequestParam(defaultValue = "1") int indexpage,
            HttpSession session
    ) {
        ModelAndView model = new ModelAndView();

        
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
        if (userId == null) {
            return new ModelAndView("redirect:/login/login");
        }
        
        //String userId = "test1"; //하드코딩 테스트용
        //String name = "테스트";//하드코딩 테스트용

        Page<MailDto> mailPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            switch (searchType) {
                case "receiver":
                    mailPage = mailService.searchDraftMailByReceiver(userId, keyword, indexpage - 1, 10);
                    break;
                case "subject":
                    mailPage = mailService.searchDraftMailBySubject(userId, keyword, indexpage - 1, 10);
                    break;
                case "content":
                    mailPage = mailService.searchDraftMailByContent(userId, keyword, indexpage - 1, 10);
                    break;
                case "date":
                    mailPage = mailService.searchDraftMailByDate(userId, keyword, indexpage - 1, 10);
                    break;
                default:
                    mailPage = mailService.searchDraftMail(userId, keyword, indexpage - 1, 10);
            }
        } else {
            mailPage = mailService.getDraftMailsPaged(userId, indexpage - 1, 10);
        }

        // 이름 포함된 MailListDto로 변환
        Page<MailListDto> page = mailService.convertToMailListDto(mailPage, userId);

        int startPageRownum = (int) (page.getTotalElements() - page.getNumber() * 10);
        int pageBlockSize = 10;
        int startBlockPage = ((indexpage - 1) / pageBlockSize) * pageBlockSize + 1;
        int endBlockPage = Math.min(startBlockPage + pageBlockSize - 1, page.getTotalPages());

        model.addObject("startBlockPage", startBlockPage);
        model.addObject("endBlockPage", endBlockPage);
        model.addObject("plist", page.getContent());
        model.addObject("startPageRownum", startPageRownum);
        model.addObject("ptotal", page.getTotalElements());
        model.addObject("ptotalPage", page.getTotalPages());
        model.addObject("currentPage", indexpage);
        model.addObject("keyword", keyword);
        model.addObject("searchType", searchType);
        model.addObject("userId", userId);
        model.addObject("name", name);

        model.setViewName("mail/maildrafts");
        return model;
    }

    
    

    // 임시보관함 전체삭제 (휴지통 이동)
    @PostMapping("/maildrafts/deleteAllToTrash")
    public String deleteAllDraftsToTrash(HttpSession session) {
      
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
    	//String userId ="test1";  //하드코딩 테스트용
    	//String name ="테스트";  //하드코딩 테스트용

        mailService.moveAllDraftsToTrash(userId);
        return "redirect:/mail/mailtrash";
    }
    
    //임시보관함 메일쓰기
    @GetMapping("/maildrafts/reply/{mailno}")
    public ModelAndView editReplyDraft(@PathVariable Long mailno, HttpSession session) {
      
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
        if (userId == null) {
            return new ModelAndView("redirect:/login/login");
        }

        // String userId = "test1"; // //하드코딩 테스트용

        MailDto draft = mailService.findByIdAndSenderId(mailno, userId);
 
        if (draft == null || !"Y".equals(draft.getMailDraft())) {
            return new ModelAndView("redirect:/maildrafts");
        }

        ModelAndView model = new ModelAndView("mail/mailwrite"); // mailwrite.jsp(.html)
        model.addObject("draft", draft);
        return model;
    }

 
   // 메일 차단 (차단할 사용자 ID를 PathVariable로 받음)
    @PostMapping("/block/{blockedId}")
    public String blockUser(@PathVariable String blockedId, HttpSession session) {
        // 실제 배포 시 세션 처리 주석 해제
      
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");

        //String userId = "test1"; // 테스트용 하드코딩

        mailService.blockUser(userId, blockedId);

        return "redirect:/mail";  // 차단 후 메일함으로 이동
    }
    
    
    // 단일 차단 해제
    @PostMapping("/unblock/{blockedId}")
    public String unblockUser(@PathVariable String blockedId, HttpSession session) {
        // 실제 배포 시 세션에서 로그인 사용자 ID 가져오기
       
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");

        //String userId = "test1"; // 테스트용 하드코딩

        mailService.unblockUser(userId, blockedId);

        return "redirect:/mail/mailblock";  // 차단 목록 화면으로 리다이렉트
    }

    // 선택한 메일 발신자 기준 여러 차단 해제
    @PostMapping("/unblock/selected")
    public ResponseEntity<?> unblockSelected(@RequestParam List<Long> mailIds, HttpSession session) {
        // 실제 배포 시 세션에서 로그인 사용자 ID 가져오기
     
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");

        //String userId = "test1"; // //하드코딩 테스트용

        try {
            for(Long mailno : mailIds) {
                MailDto mail = mailRepository.findById(mailno).orElse(null);
                if(mail != null) {
                    mailService.unblockUser(userId, mail.getSenderId());
                }
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
	 // 전체 차단 해제 
	    @PostMapping("/unblock/all")
	    public ResponseEntity<?> unblockAll(HttpSession session) {
	        // 실제 배포 시 세션 처리 주석 해제
	      
	        String name = (String) session.getAttribute("name");
	        String userId = (String) session.getAttribute("userid");
	    	//String userId ="test1"; // //하드코딩 테스트용 
	    	//String name ="테스트"; // //하드코딩 테스트용
	
	        try {
	            mailService.unblockAll(userId);
	            return ResponseEntity.ok().build();
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }
	    
	    //차단목록 전체삭제(휴지통이동)
	  

	    @PostMapping("/mailblock/deleteAllToTrash")
	    public ResponseEntity<?> moveAllBlockedMailsToTrash(HttpSession session) {
	        String userId = "test1"; // 실제는 세션에서 받아오기

	        try {
	            mailService.moveBlockedMailsToTrash(userId);
	            return ResponseEntity.ok().build();
	        } catch(Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("휴지통 이동 실패");
	        }
	    }

    
    // 차단 목록 화면 (차단 리스트 출력)
    @GetMapping("/mailblock")
    public ModelAndView getBlockedMails(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int indexpage,
            HttpSession session
    ) {
        ModelAndView model = new ModelAndView();

        // 실제 배포 시 주석 해제
     
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");

        //String userId = "test1"; //하드코딩 테스트용
        //String name = "테스트";////하드코딩 테스트용

        Page<MailDto> mailPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            mailPage = mailService.searchBlockedMails(userId, keyword, indexpage - 1, 10);
        } else {
            mailPage = mailService.getBlockedMailsPaged(userId, indexpage - 1, 10);
        }

        // 이름 포함된 MailListDto로 변환
        Page<MailListDto> page = mailService.convertToMailListDto(mailPage, userId);

        int startPageRownum = (int) (page.getTotalElements() - page.getNumber() * 10);
        int pageBlockSize = 10;
        int startBlockPage = ((indexpage - 1) / pageBlockSize) * pageBlockSize + 1;
        int endBlockPage = Math.min(startBlockPage + pageBlockSize - 1, page.getTotalPages());

        model.addObject("startBlockPage", startBlockPage);
        model.addObject("endBlockPage", endBlockPage);
        model.addObject("plist", page.getContent());
        model.addObject("startPageRownum", startPageRownum);
        model.addObject("ptotal", page.getTotalElements());
        model.addObject("ptotalPage", page.getTotalPages());
        model.addObject("currentPage", indexpage);
        model.addObject("keyword", keyword);
        model.addObject("userId", userId);
        model.addObject("name", name);

        model.setViewName("mail/mailblock");
        return model;
    }


    // 회신
    @GetMapping("/reply")
    public ModelAndView reply(@RequestParam String receiverId,
                              @RequestParam String subject,
                              HttpSession session) {
    	
    	   // 실제 배포 시 세션 처리 주석 해제
      
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
    	//String userId ="test1"; //하드코딩 테스트용 
    	//String name ="테스트";  //하드코딩 테스트용
        if (userId == null) {
            return new ModelAndView("redirect:/login/login");
        }
        

        // 아이디 => 이메일 주소 변환 로직 (필요시)
        String receiverEmail = receiverId + "@example.com";

        if (!subject.toLowerCase().startsWith("re:")) {
            subject = "RE: " + subject;
        }

        MailDto draft = new MailDto();
        draft.setReceiverId(receiverEmail);  // 이메일 주소로 세팅
        draft.setSubject(subject);
        draft.setSenderId(userId);

        ModelAndView mv = new ModelAndView("mail/mailwrite");
        mv.addObject("draft", draft);
        mv.addObject("replyMode", true);

        return mv;
    }

    // 임시보관함 -> 메일쓰기 이동 
    @GetMapping("/maildrafts/{mailno}")
    public ModelAndView openDraftMailForm(@PathVariable Long mailno, HttpSession session) {
        ModelAndView mv = new ModelAndView();

        //String userId ="test1";  //하드코딩 테스트용
    	//String name ="테스트"; //하드코딩 테스트용
        String name = (String) session.getAttribute("name");
        String userId = (String) session.getAttribute("userid");
        if (userId == null) {
            return new ModelAndView("redirect:/login/login");
        }

        MailDto mail = mailService.getMailById(mailno);

        if (mail == null || !mail.getSenderId().equals(userId) || !"Y".equals(mail.getMailDraft())) {
            mv.setViewName("redirect:/mail/maildrafts");
            return mv;
        }

        mv.addObject("draft", mail);
        mv.addObject("replyMode", false);  // 이 부분 꼭 추가해야 템플릿 오류 안남
        mv.setViewName("mail/mailwrite"); // 메일쓰기 폼 뷰

        return mv;
    }
    
    
  // 메일 작성 - 사용자명 또는 부서명(@이름 or @부서명) 자동완성
    @GetMapping("/searchUser")
    @ResponseBody
    public List<UserDto> searchUsers(@RequestParam String query, HttpSession session) {
         String userId = (String) session.getAttribute("userId");
        //String userId = "test1"; 하드코딩 테스트용 

        List<UserDto> result = new ArrayList<>();

        //  부서명 검색 (@부서명 형태 입력 시)
        if (query != null && !query.trim().isEmpty()) {
            String cleanQuery = query.trim().replaceFirst("^@", "");  

            List<CodeDto> matchedDepts = codeRepository.findByNcodeContainingAndState(cleanQuery, "Y");

            for (CodeDto dept : matchedDepts) {
                String teamName = dept.getNcode(); 
                String teamEmail = teammailRepository.findTeamMailByTeamName(teamName);

                UserDto deptUser = new UserDto();
                deptUser.setUserid("@dept:" + dept.getNcode());
                deptUser.setName(dept.getNcode());
                deptUser.setEmail(teamEmail); // 대표 메일 세팅
                result.add(deptUser);
            }
        }

        // 사용자 검색 (자기 자신 제외)
        List<UserDto> users = userService.searchUsersWithDept(query).stream()
            .filter(user -> !user.getUserid().equals(userId))
            .collect(Collectors.toList());

        result.addAll(users);

        return result;
    }

    
    
}