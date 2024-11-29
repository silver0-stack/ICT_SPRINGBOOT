package org.myweb.first.chat.jpa.repository;

import org.myweb.first.chat.jpa.entity.ChatMessageEntity;
import org.myweb.first.chat.model.dto.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessageEntity, String> {
    @Query("SELECT c FROM ChatMessageEntity c WHERE c.conversationId = :conversationId ORDER BY c.msgSentAt ASC")
    List<ChatMessageEntity> findAllByConversationId(@Param("conversationId") String conversationId);
}
