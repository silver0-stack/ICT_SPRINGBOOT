package org.myweb.first.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.chat.jpa.entity.ChatMessageEntity;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private UUID msgId;
    private String msgSenderRole;
    private String msgContent;
    private Timestamp msgSentAt;
    private String msgSenderUUID;
    private String conversationId; // 대화 그룹 ID
    private UUID parentMsgId; // 상위 메시지 ID

    public ChatMessageEntity toEntity(){
        return ChatMessageEntity.builder()
                .msgId(this.msgId)
                .msgSenderRole(this.msgSenderRole)
                .msgContent(this.msgContent)
                .msgSentAt(this.msgSentAt)
                .msgSenderUUID(this.msgSenderUUID)
                .conversationId(this.conversationId)
                .parentMsgId(this.parentMsgId)
                .build();
    }
}
