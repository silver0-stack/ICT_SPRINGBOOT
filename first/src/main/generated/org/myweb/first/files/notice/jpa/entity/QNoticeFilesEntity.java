package org.myweb.first.files.notice.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNoticeFilesEntity is a Querydsl query type for NoticeFilesEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNoticeFilesEntity extends EntityPathBase<NoticeFilesEntity> {

    private static final long serialVersionUID = 1287904987L;

    public static final QNoticeFilesEntity noticeFilesEntity = new QNoticeFilesEntity("noticeFilesEntity");

    public final StringPath nfId = createString("nfId");

    public final StringPath nfNotId = createString("nfNotId");

    public final StringPath nfOriginalName = createString("nfOriginalName");

    public final StringPath nfRename = createString("nfRename");

    public QNoticeFilesEntity(String variable) {
        super(NoticeFilesEntity.class, forVariable(variable));
    }

    public QNoticeFilesEntity(Path<? extends NoticeFilesEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNoticeFilesEntity(PathMetadata metadata) {
        super(NoticeFilesEntity.class, metadata);
    }

}

