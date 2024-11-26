package org.myweb.first.chat.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChatMessageEntity is a Querydsl query type for ChatMessageEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatMessageEntity extends EntityPathBase<ChatMessageEntity> {

    private static final long serialVersionUID = 1312613218L;

    public static final QChatMessageEntity chatMessageEntity = new QChatMessageEntity("chatMessageEntity");

    public final StringPath conversationId = createString("conversationId");

    public final StringPath msgContent = createString("msgContent");

    public final ComparablePath<java.util.UUID> msgId = createComparable("msgId", java.util.UUID.class);

    public final StringPath msgSenderRole = createString("msgSenderRole");

    public final StringPath msgSenderUUID = createString("msgSenderUUID");

    public final DateTimePath<java.sql.Timestamp> msgSentAt = createDateTime("msgSentAt", java.sql.Timestamp.class);

    public final ComparablePath<java.util.UUID> parentMsgId = createComparable("parentMsgId", java.util.UUID.class);

    public QChatMessageEntity(String variable) {
        super(ChatMessageEntity.class, forVariable(variable));
    }

    public QChatMessageEntity(Path<? extends ChatMessageEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatMessageEntity(PathMetadata metadata) {
        super(ChatMessageEntity.class, metadata);
    }

}

