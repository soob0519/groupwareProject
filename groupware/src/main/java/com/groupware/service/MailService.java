package com.groupware.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.groupware.dto.MailListDto;
import com.groupware.entity.BlockDto;
import com.groupware.entity.CodeDto;
import com.groupware.entity.EmpDto;
import com.groupware.entity.MailDto;
import com.groupware.entity.TeamMailDto;
import com.groupware.repository.BlockRepository;
import com.groupware.repository.CodeRepository;
import com.groupware.repository.EmpRepository;
import com.groupware.repository.MailRepository;
import com.groupware.repository.TeamMailRepository;

import jakarta.transaction.Transactional;


@Service
public class MailService {

    private final MailRepository mailRepository;
    private final EmpRepository empRepository;
    private final CodeRepository codeRepository;
    private final BlockRepository blockRepository;
    private final TeamMailRepository teamMailRepository;


    public MailService(MailRepository mailRepository,
    		CodeRepository codeRepository,
    		EmpRepository empRepository,
    		BlockRepository blockRepository,
    		TeamMailRepository teamMailRepository
    		) {
        this.mailRepository = mailRepository;
        this.codeRepository = codeRepository;
        this.empRepository = empRepository;
        this.blockRepository = blockRepository; 
        this.teamMailRepository = teamMailRepository;
    }
    
   // 발신, 수신, 이름(부서) 변환 (부서전체메일시 ex) @영업부 표시)
    public Page<MailListDto> convertToMailListDto(Page<MailDto> mailPage, String loginUserId) {
        return mailPage.map(mail -> {
            MailListDto dto = new MailListDto();
            dto.setMailno(mail.getMailno());
            dto.setSubject(mail.getSubject());
            dto.setSentAt(mail.getSentAt());

            boolean isSender = mail.getSenderId().equals(loginUserId);
            dto.setDirection(isSender ? "발신" : "수신");

            String targetId = isSender ? mail.getReceiverId() : mail.getSenderId();
            String targetEmail = isSender ? mail.getMailReceiver() : mail.getMailSender();

            try {
                // 1. 내부 그룹메일 형태: @dept:영업부 or @영업부
                if (targetId != null && targetId.startsWith("@")) {
                    String deptCode = targetId.startsWith("@dept:") ? targetId.substring(6) : targetId.substring(1);
                    CodeDto code = codeRepository.findById(deptCode).orElse(null);
                    if (code != null && "Y".equals(code.getState())) {
                        dto.setName( code.getNcode()); 
                    } else {
                        dto.setName(targetId); // fallback
                    }

                // 2. 내부 대표메일일 경우: sales@example.com → @영업부
                } else if (targetEmail != null) {
                    TeamMailDto team = teamMailRepository.findByTeamMail(targetEmail).orElse(null);
                    if (team != null) {
                        dto.setName( team.getTeamName()); // ex) @영업부
                    } else {
                        // 3. 일반 사용자: name (부서명)
                        EmpDto emp = empRepository.findByUserid(targetId).orElse(null);
                        if (emp != null && emp.getName() != null) {
                            String deptName = "-";
                            if (emp.getDept() != null) {
                                CodeDto code = codeRepository.findById(emp.getDept()).orElse(null);
                                if (code != null && "Y".equals(code.getState()) && code.getNcode() != null) {
                                    deptName = code.getNcode();
                                }
                            }
                            dto.setName(emp.getName() + " (" + deptName + ")");
                        } else {
                            // 4. 외부 이메일 주소 그대로
                            dto.setName(targetEmail);
                        }
                    }
                } else {
                    dto.setName(""); // fallback
                }

            } catch (Exception e) {
                System.out.println("에러 발생: " + e.getMessage());
                dto.setName("오류발생");
            }

            return dto;
        });
    }

 // 전체 메일 리스트 조회 (삭제, 차단 안 된 내 메일 + 부서 대표메일 포함)
    public Page<MailDto> listWithName(String loginUserId, List<String> deptEmails, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        return mailRepository.findByUserInvolvedWithNoDeleteNoBlockWithDept(loginUserId, deptEmails, pageable);
    }

    // 전체메일 - 제목 검색
    public Page<MailDto> searchBySubject(String userId, List<String> deptEmails, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        return mailRepository.findByUserAndSubjectContainingWithDept(userId, deptEmails, keyword, pageable);
    }

    // 전체메일 - 내용 검색
    public Page<MailDto> searchByContent(String userId, List<String> deptEmails, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        return mailRepository.findByUserAndContentContainingWithDept(userId, deptEmails, keyword, pageable);
    }

    // 전체메일 - 발신자명 검색
    public Page<MailDto> searchBySenderName(String userId, List<String> deptEmails, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        return mailRepository.searchBySenderNameWithDept(userId, deptEmails, keyword, pageable);
    }

    // 전체메일 - 수신자명 검색
    public Page<MailDto> searchByReceiverName(String userId, List<String> deptEmails, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        return mailRepository.searchByReceiverNameWithDept(userId, deptEmails, keyword, pageable);
    }

    // 전체메일 - 날짜 검색
    public Page<MailDto> searchByDate(String userId, List<String> deptEmails, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        try {
            LocalDate date = LocalDate.parse(keyword);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            return mailRepository.findByUserAndSentAtBetweenWithDept(userId, deptEmails, start, end, pageable);
        } catch (DateTimeParseException e) {
            return Page.empty(pageable);
        }
    }

    // 전체메일 기본 검색 (내용 기준)
    public Page<MailDto> search(String userId, List<String> deptEmails, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        return mailRepository.findByUserAndContentContainingWithDept(userId, deptEmails, keyword, pageable);
    }

    
    //메일 디테일 
    public MailDto findByIdAndUserId(Long mailno, String userId) {
        MailDto mail = mailRepository.findById(mailno)
            .orElseThrow(() -> new RuntimeException("메일을 찾을 수 없습니다."));
        
        if (!userId.equals(mail.getSenderId()) && !userId.equals(mail.getReceiverId())) {
            throw new RuntimeException("메일에 접근할 권한이 없습니다.");
        }
        
        return mail;
    }

    // 단일삭제 (휴지통 이동)
    @Transactional
    public void softDeleteByUser(Long mailno, String userId) {
        MailDto mail = mailRepository.findById(mailno)
            .orElseThrow(() -> new RuntimeException("메일을 찾을 수 없습니다."));

        if (userId.equals(mail.getSenderId())) {
            mailRepository.softDeleteBySender(mailno, userId); // 사용자가 발신자 휴지통이동
        } else if (userId.equals(mail.getReceiverId())) {
            mailRepository.softDeleteByReceiver(mailno, userId); // 사용자가 수신자 휴지통이동
        } else {
            throw new RuntimeException("해당 메일에 대한 권한이 없습니다.");
        }
    }

    //메일 다중삭제 (휴지통 이동)
    public void deleteMailsByIds(List<Long> mailIds, String userId) {
        for (Long mailId : mailIds) {
            // 먼저 발신자인 경우 삭제
            int senderResult = mailRepository.softDeleteBySender(mailId, userId);

            // 아니면 수신자인 경우 삭제
            if (senderResult == 0) {
                mailRepository.softDeleteByReceiver(mailId, userId);
            }
        }
    }
    
   //메일 전체삭제 (휴지통 이동)
    public void moveAllMailsToTrash(String userId) {
        List<Long> mailIds = mailRepository.findAllIdsByUser(userId);
        for (Long mailId : mailIds) {
            int senderResult = mailRepository.softDeleteBySender(mailId, userId);
            if (senderResult == 0) {
                mailRepository.softDeleteByReceiver(mailId, userId);
            }
        }
    }

    // 수신메일함 - 기본 목록
    public Page<MailListDto> receivedListWithName(String loginUserId, int page, int size) {
        Page<MailDto> mailPage = mailRepository.findByReceiverIdAndMailDeleteReceiverExcludeSelf(
            loginUserId,
            "N",
            PageRequest.of(page, size, Sort.by("sentAt").descending())
        );
        return convertToMailListDto(mailPage, loginUserId);
    }

    // 수신메일함 - 발신자 이름 검색
    public Page<MailListDto> searchReceivedBySenderName(String userId, String keyword, int page, int size) {
        Page<MailDto> mails = mailRepository.searchReceivedBySenderNameExcludeSelf(
            userId,
            keyword,
            PageRequest.of(page, size, Sort.by("sentAt").descending())
        );
        return convertToMailListDto(mails, userId);
    }

    // 수신메일함 - 제목 검색
    public Page<MailListDto> searchReceivedBySubject(String userId, String keyword, int page, int size) {
        Page<MailDto> mails = mailRepository.findByReceiverIdAndSubjectContainingAndMailDeleteReceiverExcludeSelf(
            userId,
            keyword,
            "N",
            PageRequest.of(page, size, Sort.by("sentAt").descending())
        );
        return convertToMailListDto(mails, userId);
    }

    // 수신메일함 - 내용 검색
    public Page<MailListDto> searchReceivedByContent(String userId, String keyword, int page, int size) {
        Page<MailDto> mails = mailRepository.findByReceiverIdAndContentContainingAndMailDeleteReceiverExcludeSelf(
            userId,
            keyword,
            "N",
            PageRequest.of(page, size, Sort.by("sentAt").descending())
        );
        return convertToMailListDto(mails, userId);
    }

    // 수신메일함 -날짜 검색
    public Page<MailListDto> searchReceivedByDate(String userId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());

        LocalDate date;
        try {
            date = LocalDate.parse(keyword);
        } catch (DateTimeParseException e) {
            return Page.empty(pageable);
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        Page<MailDto> mailPage = mailRepository.findByReceiverIdAndSentAtBetweenAndMailDeleteReceiverExcludeSelf(
            userId,
            start,
            end,
            "N",
            pageable
        );
        return convertToMailListDto(mailPage, userId);
    }

    // 수신메일함 - 기본 검색 (내용)
    public Page<MailListDto> searchReceived(String userId, String keyword, int page, int size) {
        Page<MailDto> mails = mailRepository.findByReceiverIdAndContentContainingAndMailDeleteReceiverExcludeSelf(
            userId,
            keyword,
            "N",
            PageRequest.of(page, size, Sort.by("sentAt").descending())
        );
        return convertToMailListDto(mails, userId);
    }

	 // 수신메일 다중삭제 (휴지통 이동)
    @Transactional
    public void deleteReceivedMailsByIds(String userId, List<Long> mailIds) {
        List<MailDto> mails = mailRepository.findAllById(mailIds);
        for (MailDto mail : mails) {
            if (userId.equals(mail.getReceiverId())) {
                mail.setMailDeleteReceiver("Y"); // 수신자 삭제 표시
            }
        }
        mailRepository.saveAll(mails);
    }


    //수신메일 전체삭제 (휴지통 이동)
    @Transactional
    public void moveAllReceivedMailsToTrash(String userId) {
        List<MailDto> receivedMails = mailRepository.findByReceiverIdAndMailDeleteReceiver(userId, "N");
        for (MailDto mail : receivedMails) {
            mail.setMailDeleteReceiver("Y");
        }
        mailRepository.saveAll(receivedMails);
    }


 // 발신메일함 기본 목록 (내가 보낸 메일 중 삭제 안 된 것만, 나한테 보낸 메일 제외)
    public Page<MailListDto> sendListWithName(String loginUserId, int page, int size) {
        Page<MailDto> mailPage = mailRepository.findBySenderIdAndMailDeleteSenderExcludeSelf(
            loginUserId, "N", PageRequest.of(page, size, Sort.by("sentAt").descending())
        );
        return convertToMailListDto(mailPage, loginUserId);
    }

    // 발신메일함 - 수신자 이름으로 검색 (EmpDto 조인)
    public Page<MailListDto> searchSendByReceiverName(String userId, String keyword, int page, int size) {
        Page<MailDto> mails = mailRepository.searchSendByReceiverName(
            userId, keyword, PageRequest.of(page, size, Sort.by("sentAt").descending())
        );
        return convertToMailListDto(mails, userId);
    }

    // 발신메일함 - 제목 검색
    public Page<MailListDto> searchSendBySubject(String userId, String keyword, int page, int size) {
        Page<MailDto> mails = mailRepository.findBySenderIdAndSubjectContainingAndMailDeleteSender(
            userId, keyword, "N", PageRequest.of(page, size, Sort.by("sentAt").descending())
        );
        return convertToMailListDto(mails, userId);
    }

    // 발신메일함 - 내용 검색
    public Page<MailListDto> searchSendByContent(String userId, String keyword, int page, int size) {
        Page<MailDto> mails = mailRepository.findBySenderIdAndContentContainingAndMailDeleteSender(
            userId, keyword, "N", PageRequest.of(page, size, Sort.by("sentAt").descending())
        );
        return convertToMailListDto(mails, userId);
    }

    // 발신메일함 - 날짜 검색
    public Page<MailListDto> searchSendByDate(String userId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());

        LocalDate date;
        try {
            date = LocalDate.parse(keyword);
        } catch (DateTimeParseException e) {
            return Page.empty(pageable);
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        Page<MailDto> mailPage = mailRepository.findBySenderIdAndSentAtBetweenAndMailDeleteSender(
            userId, start, end, "N", pageable
        );
        return convertToMailListDto(mailPage, userId);
    }

    // 발신메일함 - 기본 검색 (내용 기준)
    public Page<MailListDto> searchSend(String userId, String keyword, int page, int size) {
        Page<MailDto> mails = mailRepository.findBySenderIdAndContentContainingAndMailDeleteSender(
            userId, keyword, "N", PageRequest.of(page, size, Sort.by("sentAt").descending())
        );
        return convertToMailListDto(mails, userId);
    }

	 // 발신메일 다중삭제 (휴지통 이동)
	    @Transactional
	    public void deleteSentMailsByIds(String userId, List<Long> mailIds) {
	        List<MailDto> mails = mailRepository.findAllById(mailIds);
	        for (MailDto mail : mails) {
	            if (userId.equals(mail.getSenderId())) {
	                mail.setMailDeleteSender("Y"); // 발신자 삭제 표시
	            }
	        }
	        mailRepository.saveAll(mails);
	    }
	
	    // 발신메일 전체삭제 (휴지통 이동)
	    @Transactional
	    public void moveAllSentMailsToTrash(String userId) {
	        List<MailDto> sentMails = mailRepository.findBySenderIdAndMailDeleteSender(userId, "N");
	        for (MailDto mail : sentMails) {
	            mail.setMailDeleteSender("Y"); // 발신자 삭제 표시
	        }
	        mailRepository.saveAll(sentMails);
	    }

		  // 휴지통 - 기본 목록
	     public Page<MailListDto> trashList(String userId, int page, int size) {
	         Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
	         Page<MailDto> mails = mailRepository.findTrashMails(userId, pageable);
	         return convertToMailListDto(mails, userId);
	     }

		
		  // 휴지통 - 제목 검색
		  public Page<MailListDto> searchTrashBySubject(String userId, String keyword, int page, int size) {
		      Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
		      Page<MailDto> mails = mailRepository.searchTrashBySubject(userId, keyword, pageable);
		      return convertToMailListDto(mails, userId);
		  }
		
		  // 휴지통 - 내용 검색
		  public Page<MailListDto> searchTrashByContent(String userId, String keyword, int page, int size) {
		      Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
		      Page<MailDto> mails = mailRepository.searchTrashByContent(userId, keyword, pageable);
		      return convertToMailListDto(mails, userId);
		  }
		
		  // 휴지통 - 날짜 검색
		  public Page<MailListDto> searchTrashByDate(String userId, String keyword, int page, int size) {
		      Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
		
		      try {
		          LocalDate date = LocalDate.parse(keyword);
		          LocalDateTime start = date.atStartOfDay();
		          LocalDateTime end = date.plusDays(1).atStartOfDay();
		
		          Page<MailDto> mails = mailRepository.searchTrashByDate(userId, start, end, pageable);
		          return convertToMailListDto(mails, userId);
		      } catch (DateTimeParseException e) {
		          return Page.empty(pageable);
		      }
		  }
		
		// 휴지통 - 발신자 이름 검색
		  public Page<MailListDto> searchTrashBySenderName(String userId, String keyword, int page, int size) {
		      Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
		      Page<MailDto> mails = mailRepository.searchTrashBySenderName(userId, keyword, pageable);
		      return convertToMailListDto(mails, userId);
		  }

		  // 휴지통 - 수신자 이름 검색
		  public Page<MailListDto> searchTrashByReceiverName(String userId, String keyword, int page, int size) {
		      Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
		      Page<MailDto> mails = mailRepository.searchTrashByReceiverName(userId, keyword, pageable);
		      return convertToMailListDto(mails, userId);
		  }


		  // 휴지통 선택복구
		  @Transactional
		  public void restoreMails(List<Long> mailIds, String userId) {
		      for (Long id : mailIds) {
		          MailDto mail = mailRepository.findById(id)
		              .orElseThrow(() -> new RuntimeException("메일 없음"));

		          if (userId.equals(mail.getSenderId())) {
		              mail.setMailDeleteSender("N");
		          }

		          if (userId.equals(mail.getReceiverId())) {
		              mail.setMailDeleteReceiver("N");
		          }
		      }
		  }
		  
		
		// 휴지통 선택삭제
		  @Transactional
		  public void deleteMails(List<Long> mailIds, String userId) {
		      for (Long id : mailIds) {
		          MailDto mail = mailRepository.findById(id)
		              .orElseThrow(() -> new RuntimeException("메일 없음: " + id));

		          // 로그인 유저가 보낸 사람이면 보낸 쪽 완전삭제 처리
		          if (userId.equalsIgnoreCase(mail.getSenderId())) {
		              mail.setMailFullyDeletedSender("Y");
		          }

		          // 로그인 유저가 받은 사람이면 받은 쪽 완전삭제 처리
		          if (userId.equalsIgnoreCase(mail.getReceiverId())) {
		              mail.setMailFullyDeletedReceiver("Y");
		          }

		          // 양쪽 다 완전삭제 상태일 때 실제 DB에서 삭제
		          if ("Y".equals(mail.getMailFullyDeletedSender()) && "Y".equals(mail.getMailFullyDeletedReceiver())) {
		              mailRepository.deleteById(id);
		          }
		      }
		  }


		   // 검색결과 있을때 휴지통 전체삭제 
		    public List<MailDto> findTrashMailsByUserAndKeyword(String userId, String keyword, String searchType) {
		        switch (searchType) {
		            case "subject":
		                return mailRepository.searchTrashBySubjectAll(userId, keyword);
		            case "content":
		                return mailRepository.searchTrashByContentAll(userId, keyword);
		            case "sender":
		                return mailRepository.searchTrashBySenderNameAll(userId, keyword);
		            case "receiver":
		                return mailRepository.searchTrashByReceiverNameAll(userId, keyword);
		            default:
		                return mailRepository.findTrashMailsAll(userId);
		        }
		    }

		    // 휴지통 전체 복구
		    @Transactional
		    public void restoreAll(String userId, String keyword, String searchType) {
		        List<MailDto> mails = findTrashMailsByUserAndKeyword(userId, keyword, searchType);
		        for (MailDto mail : mails) {
		            if (userId.equals(mail.getSenderId())) {
		                mail.setMailDeleteSender("N");
		            }
		            if (userId.equals(mail.getReceiverId())) {
		                mail.setMailDeleteReceiver("N");
		            }
		        }
		    }
		    // 휴지통 전체 삭제
		    @Transactional
		    public void deleteAll(String userId, String keyword, String searchType) {
		        List<MailDto> mails = findTrashMailsByUserAndKeyword(userId, keyword, searchType);
		        for (MailDto mail : mails) {
		            if (userId.equals(mail.getSenderId())) {
		                mail.setMailFullyDeletedSender("Y");
		            }
		            if (userId.equals(mail.getReceiverId())) {
		                mail.setMailFullyDeletedReceiver("Y");
		            }
		            if ("Y".equals(mail.getMailFullyDeletedSender()) && "Y".equals(mail.getMailFullyDeletedReceiver())) {
		            	mailRepository.deleteById(mail.getMailno());
		            }
		        }
		    }

		    //메일 전송
	        public void sendMail(MailDto mailDto) {
	            String receiver = mailDto.getReceiverId();
	            if (receiver != null && receiver.contains("@")) {
	                receiver = receiver.substring(0, receiver.indexOf("@"));
	                mailDto.setReceiverId(receiver);
	            }

	            mailRepository.save(mailDto);
	            // 추가 발송 로직 필요하면 작성
	        }
		    
	     

	     // 내게 쓴 메일함 - 기본 목록 조회
	     
	     public Page<MailListDto> mailToMeListWithName(String userId, int page, int size) {
	         Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending()); // 페이징 + 정렬 설정
	         Page<MailDto> mailPage = mailRepository.findMailToMeByUserId(userId, pageable);  // 레포지토리 호출
	         return convertToMailListDto(mailPage, userId); // DTO 변환 후 반환
	     }

	     // 내게 쓴 메일함 - 제목 검색
	      
	     public Page<MailListDto> searchMailToMeBySubject(String userId, String keyword, int page, int size) {
	         Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
	         Page<MailDto> mailPage = mailRepository.searchMailToMeBySubject(userId, keyword, pageable);
	         return convertToMailListDto(mailPage, userId);
	     }

	     // 내게 쓴 메일함 - 내용 검색
	     
	     public Page<MailListDto> searchMailToMeByContent(String userId, String keyword, int page, int size) {
	         Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
	         Page<MailDto> mailPage = mailRepository.searchMailToMeByContent(userId, keyword, pageable);
	         return convertToMailListDto(mailPage, userId);
	     }

	     // 내게 쓴 메일함 - 날짜 검색
	     
	     public Page<MailListDto> searchMailToMeByDate(String userId, String keyword, int page, int size) {
	         Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());

	         LocalDate date;
	         try {
	             date = LocalDate.parse(keyword); // 문자열을 날짜로 변환 시도
	         } catch (DateTimeParseException e) {
	             return Page.empty(pageable); // 파싱 실패 시 빈 페이지 반환
	         }

	         LocalDateTime start = date.atStartOfDay(); // 검색 시작 시간 (00:00:00)
	         LocalDateTime end = date.plusDays(1).atStartOfDay(); // 검색 종료 시간 (다음날 00:00:00)

	         Page<MailDto> mailPage = mailRepository.searchMailToMeByDate(userId, start, end, pageable);
	         return convertToMailListDto(mailPage, userId);
	     }

	     // 내게 쓴 메일함 - 기본 검색
	    
	     public Page<MailListDto> searchMailToMe(String userId, String keyword, int page, int size) {
	         Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());

	         Page<MailDto> mailPage = mailRepository.findBySenderIdAndReceiverIdAndMailDeleteSenderAnd(
	             userId, userId, "N", keyword, pageable);

	         return convertToMailListDto(mailPage, userId);
	     }


	     // 내게쓴메일함 전체삭제 
	     @Transactional
	     public void deleteAllMailToMe(String userId) {
	    	    // senderId=receiverId=userId, mailDeleteSender='N' 인 메일들만 삭제(휴지통으로 이동)
	    	    mailRepository.markDeletedMailToMe(userId);
	    	}
	     
	     
	   //메일 임시보관
	     public void saveDraft(MailDto dto, String userId) {
	         dto.setSenderId(userId);
	         dto.setMailDraft("Y");  // 임시저장 표시
	         // receiverId가 null이면 빈 문자열로 세팅 (DB not-null이면 오류 방지)
	         if (dto.getReceiverId() == null) {
	             dto.setReceiverId(" ");
	         }
	 

	         mailRepository.save(dto);
	     }

	     // 메일 임시보관함 리스트
	     public Page<MailDto> getDraftMailsPaged(String senderId, int page, int size) {
	    	    return mailRepository.findDraftMailsBySenderIdAndNotDeleted(
	    	        senderId,
	    	        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"))
	    	    );
	    	}



	     // 임시보관함 - 제목 검색
	     public Page<MailDto> searchDraftMailBySubject(String senderId, String keyword, int page, int size) {
	         return mailRepository.findBySenderIdAndMailDraftAndSubjectContaining(
	             senderId, "Y", keyword,
	             PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"))
	         );
	     }

	     // 임시보관함 - 내용 검색
	     public Page<MailDto> searchDraftMailByContent(String senderId, String keyword, int page, int size) {
	         return mailRepository.findBySenderIdAndMailDraftAndContentContaining(
	             senderId, "Y", keyword,
	             PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"))
	         );
	     }

	     // 임시보관함 - 날짜 검색
	     public Page<MailDto> searchDraftMailByDate(String senderId, String keyword, int page, int size) {
	         return mailRepository.findBySenderIdAndMailDraftAndSentAtContaining(
	             senderId, "Y", keyword,
	             PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"))
	         );
	     }

	     // 임시보관함 - 수신자 검색
	     public Page<MailDto> searchDraftMailByReceiver(String senderId, String keyword, int page, int size) {
	         return mailRepository.findBySenderIdAndMailDraftAndReceiverIdContaining(
	             senderId, "Y", keyword,
	             PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"))
	         );
	     }

	     // 임시보관함 - 제목 or 내용 통합 검색
	     public Page<MailDto> searchDraftMail(String senderId, String keyword, int page, int size) {
	         return mailRepository.searchDraftMail(
	             senderId, keyword,
	             PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"))
	         );
	     }

	    // 임시보관함 전체 메일 휴지통으로 이동 처리
	     @Transactional
	     public void moveAllDraftsToTrash(String userId) {
	         mailRepository.markAllDraftsDeletedBySender(userId);
	     }

	     
	    //임시보관함 메일쓰기
	     public MailDto findByIdAndSenderId(Long mailno, String senderId) {
	         return mailRepository.findByMailnoAndSenderIdAndMailDraft(mailno, senderId, "Y");
	     }
	     
	     
	  
	  // 메일 차단 처리
	  // 로그인한 사용자(blockerId)가 차단 대상(blockedId)를 차단함
	  // 중복 차단 방지 로직 포함
	     @Transactional
	     public void blockUser(String blockerId, String blockedId) {
	         if (!blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
	             BlockDto block = new BlockDto();
	             block.setBlockerId(blockerId);
	             block.setBlockedId(blockedId);
	             blockRepository.save(block);
	         }
	     }


	     // 차단 목록 조회
	     public List<BlockDto> getBlockList(String blockerId) {
	         return blockRepository.findAllByBlockerId(blockerId);
	     }

	     // 차단 해제 (차단 해지)
	     @Transactional
	     public void unblockUser(String blockerId, String blockedId) {
	         blockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
	             .ifPresent(blockRepository::delete);
	     }

	     // 차단 여부 확인
	     public boolean isBlocked(String blockerId, String blockedId) {
	         return blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId);
	     }

	     // 차단된 사용자로부터 받은 메일 페이징 조회
	     public Page<MailDto> getBlockedMailsPaged(String userId, int page, int size) {
	         Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
	         return mailRepository.findBlockedMails(userId, pageable);
	     }

	     // 차단된 사용자 메일 키워드 검색 (제목, 내용, 발신자명)
	     public Page<MailDto> searchBlockedMails(String userId, String keyword, int page, int size) {
	         Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
	         return mailRepository.searchBlockedMails(userId, keyword, pageable);
	     }
	  // 전체 차단 해제 
	     @Transactional
	     public void unblockAll(String blockerId) {
	         blockRepository.deleteAllByBlockerId(blockerId);
	     }

	   //차단목록 전체삭제(휴지통이동)
	     @Transactional
	     public void moveBlockedMailsToTrash(String userId) {
	         //차단된 사용자 리스트 조회
	         List<String> blockedIds = blockRepository.findBlockedIdsByBlockerId(userId);
	         
	         if(blockedIds.isEmpty()) return;
	         
	         // 수신 메일 중 차단된 사용자 메일 휴지통 이동
	         mailRepository.updateMailDeleteReceiverToY(userId, blockedIds);
	         
	         // (필요 시) 발신 메일도 휴지통 이동 처리
	         mailRepository.updateMailDeleteSenderToY(userId, blockedIds);
	     }
	     
	  // 임시보관함 -> 메일쓰기 이동 
     public MailDto getMailById(Long mailno) {
    	    return mailRepository.findById(mailno).orElse(null);
    	}

     //임시보관함 -> 메일쓰기 -> 기존내용 업데이트
     @Transactional
     public void sendFromDraft(MailDto mailDto) {
         // mailno가 있으면 기존 임시저장 메일 업데이트 + 발송 처리
         Optional<MailDto> existing = mailRepository.findByMailnoAndSenderId(mailDto.getMailno(), mailDto.getSenderId());

         if (existing.isPresent()) {
             MailDto mailToUpdate = existing.get();
             mailToUpdate.setReceiverId(mailDto.getReceiverId());
             mailToUpdate.setMailReceiver(mailDto.getMailReceiver());
             mailToUpdate.setSubject(mailDto.getSubject());
             mailToUpdate.setContent(mailDto.getContent());
             mailToUpdate.setMailDraft("N");       // 발송 상태로 변경
             mailToUpdate.setSentAt(LocalDateTime.now());
             mailRepository.save(mailToUpdate);
         } else {
             // 없으면 새로 저장
             mailRepository.save(mailDto);
         }
     }


}



