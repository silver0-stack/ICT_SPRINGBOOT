package org.myweb.first.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.chat.jpa.entity.ChatMessageEntity;

import java.sql.Timestamp;
import java.util.UUID;

@Data  //@Getter, @Setter, @ToString, @Equals, @HashCode 오버라이딩 까지 자동 생성됨
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 생성
@NoArgsConstructor // 기본 생성자 생성
@Builder // 빌더 패턴 지원
public class ChatMessage {
    private UUID msgId;
    private String msgSenderRole;
    private String msgContent;
    private Timestamp msgSentAt;
    private String msgSenderUUID;

    /*
    @Id
    @Column(name = "MSG_ID", nullable = false)
    private UUID msgId;  // MSG_ID    VARCHAR2(50 BYTE) :  메시지 ID (PK)
    @Column(name="MSG_SENDER_ROLE", nullable = false)
    private String msgSenderRole; // MSG_SENDER_ROLE
    @Column(name = "MESSAGE", nullable = false, length = 2000)
    private String msgContent; // MSG_CONTENT   CLOB: 메시지 내용
    @Column(name="MSG_SENT_AT", nullable = false)
    private Timestamp msgSentAt; // MSG_SENT_AT TIMESTAMP 전송 시각
    @Column(name = "MSG_SENDER_UUID", nullable = false, length = 50)
    private String senderId;  // SENDER_ID    VARCHAR2(50 BYTE) : 작성자 UUID (FK)
    * */

    public ChatMessageEntity toEntity(){
        return ChatMessageEntity.builder()
                .msgId(msgId)
                .msgSenderRole(msgSenderRole)
                .msgContent(msgContent)
                .msgSentAt(msgSentAt)
                .msgSenderUUID(msgSenderUUID)
                .build();
    }
}
