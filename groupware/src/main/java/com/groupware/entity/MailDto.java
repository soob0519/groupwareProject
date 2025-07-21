package com.groupware.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "MAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailDto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mail_seq_gen")
    @SequenceGenerator(name = "mail_seq_gen", sequenceName = "MAIL_SEQ", allocationSize = 1)
    @Column(name = "MAILNO")
    private Long mailno; // 메일 고유번호

    @Column(name = "SENDER_ID", nullable = false, length = 100)
    private String senderId; // 보낸 사람

    @Column(name = "RECEIVER_ID", nullable = false, length = 100)
    private String receiverId; // 받은 사람

    @Column(name = "SUBJECT", length = 255)
    private String subject; // 메일 제목

    @Column(name = "CONTENT", length = 1000)
    private String content; // 메일 내용

    @CreationTimestamp
    @Column(name = "SENT_AT", updatable = false)
    private LocalDateTime sentAt; // 메일 날짜

    @Column(name = "MAIL_READ", length = 1, nullable = false)
    private String mailRead = "N"; // 읽음 여부 (기본값 N)

    @Column(name = "MAIL_ATTACHMENT", length = 1, nullable = false)
    private String mailAttachment = "N"; // 파일 첨부 여부 (기본값 N)

    @Column(name = "MAIL_DRAFT", length = 1, nullable = false)
    private String mailDraft = "N"; // 임시저장 여부 (기본값 N)

    @Column(name = "SCHEDULED_AT")
    private LocalDateTime scheduledAt; // 예약 시간

    @Column(name = "MAIL_RECEIVER", length = 100)
    private String mailReceiver; // 팀별 수신자 이메일실제주소
    
    @Column(name = "MAIL_SENDER", length = 100)
    private String mailSender;// 팀별 발신자 이메일실제주소
 
	@Column(name = "MAIL_DELETE_SENDER", length = 1, nullable = false)
	private String mailDeleteSender = "N"; // 발신자가 메일을 삭제(휴지통 이동)했는지 여부 기본값 N

	@Column(name = "MAIL_DELETE_RECEIVER", length = 1, nullable = false)
	private String mailDeleteReceiver = "N"; // 수신자가 메일을 삭제(휴지통 이동)했는지 여부 기본값 N
	
	@Column(name = "MAIL_FULLY_DELETED_SENDER", length = 1, nullable = false)
	private String mailFullyDeletedSender = "N"; // 발신자가 메일을 '완전삭제'했는지 여부 기본값 N
	
	@Column(name = "MAIL_FULLY_DELETED_RECEIVER", length = 1, nullable = false)
	private String mailFullyDeletedReceiver = "N";// 수신자가 메일을 '완전삭제'했는지 여부 기본값 N

	@Column(name = "ATTACHMENT_NAME", length = 255)
	private String attachmentName;  // 첨부파일명 또는 경로 (서버 내 상대경로 등) 
	 
	 
	 


}
