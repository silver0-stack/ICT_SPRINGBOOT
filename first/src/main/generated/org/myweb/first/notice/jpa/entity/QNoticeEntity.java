package org.myweb.first.notice.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNoticeEntity is a Querydsl query type for NoticeEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNoticeEntity extends EntityPathBase<NoticeEntity> {

    private static final long serialVersionUID = -1252615733L;

    public static final QNoticeEntity noticeEntity = new QNoticeEntity("noticeEntity");

    public final StringPath notContent = createString("notContent");

    public final DateTimePath<java.sql.Timestamp> notCreatedAt = createDateTime("notCreatedAt", java.sql.Timestamp.class);

    public final StringPath notCreatedBy = createString("notCreatedBy");

    public final DateTimePath<java.sql.Timestamp> notDeletedAt = createDateTime("notDeletedAt", java.sql.Timestamp.class);

    public final StringPath notDeletedBy = createString("notDeletedBy");

    public final StringPath notId = createString("notId");

    public final NumberPath<Integer> notReadCount = createNumber("notReadCount", Integer.class);

    public final StringPath notTitle = createString("notTitle");

    public final DateTimePath<java.sql.Timestamp> notUpdatedAt = createDateTime("notUpdatedAt", java.sql.Timestamp.class);

    public final StringPath notUpdatedBy = createString("notUpdatedBy");

    public QNoticeEntity(String variable) {
        super(NoticeEntity.class, forVariable(variable));
    }

    public QNoticeEntity(Path<? extends NoticeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNoticeEntity(PathMetadata metadata) {
        super(NoticeEntity.class, metadata);
    }

}

