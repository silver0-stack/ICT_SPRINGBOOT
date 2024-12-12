package org.myweb.first.workspace.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkspaceEntity is a Querydsl query type for WorkspaceEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkspaceEntity extends EntityPathBase<WorkspaceEntity> {

    private static final long serialVersionUID = 1040634599L;

    public static final QWorkspaceEntity workspaceEntity = new QWorkspaceEntity("workspaceEntity");

    public final SetPath<org.myweb.first.chat.jpa.entity.ChatMessageEntity, org.myweb.first.chat.jpa.entity.QChatMessageEntity> chatMessages = this.<org.myweb.first.chat.jpa.entity.ChatMessageEntity, org.myweb.first.chat.jpa.entity.QChatMessageEntity>createSet("chatMessages", org.myweb.first.chat.jpa.entity.ChatMessageEntity.class, org.myweb.first.chat.jpa.entity.QChatMessageEntity.class, PathInits.DIRECT2);

    public final DateTimePath<java.sql.Timestamp> workspaceCreatedAt = createDateTime("workspaceCreatedAt", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> workspaceDeletedAt = createDateTime("workspaceDeletedAt", java.sql.Timestamp.class);

    public final StringPath workspaceId = createString("workspaceId");

    public final StringPath workspaceMemUuid = createString("workspaceMemUuid");

    public final StringPath workspaceName = createString("workspaceName");

    public final StringPath workspaceStatus = createString("workspaceStatus");

    public final DateTimePath<java.sql.Timestamp> workspaceUpdatedAt = createDateTime("workspaceUpdatedAt", java.sql.Timestamp.class);

    public QWorkspaceEntity(String variable) {
        super(WorkspaceEntity.class, forVariable(variable));
    }

    public QWorkspaceEntity(Path<? extends WorkspaceEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWorkspaceEntity(PathMetadata metadata) {
        super(WorkspaceEntity.class, metadata);
    }

}

