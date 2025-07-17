package com.groupware.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailListDto {

    private Long mailno;             // 메일 번호 (for detail 이동용)
    private String direction;        // "수신" or "발신"
    private String name;             // 상대방 이름
    private String team;             // 상대방 팀
    private String subject;          // 제목
    private LocalDateTime sentAt;    // 보낸 시간
   

}
