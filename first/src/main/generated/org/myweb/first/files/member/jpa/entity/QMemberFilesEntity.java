package org.myweb.first.files.member.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberFilesEntity is a Querydsl query type for MemberFilesEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberFilesEntity extends EntityPathBase<MemberFilesEntity> {

    private static final long serialVersionUID = 162158935L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberFilesEntity memberFilesEntity = new QMemberFilesEntity("memberFilesEntity");

    public final org.myweb.first.member.jpa.entity.QMemberEntity member;

    public final StringPath mfId = createString("mfId");

    public final StringPath mfOriginalName = createString("mfOriginalName");

    public final StringPath mfRename = createString("mfRename");

    public QMemberFilesEntity(String variable) {
        this(MemberFilesEntity.class, forVariable(variable), INITS);
    }

    public QMemberFilesEntity(Path<? extends MemberFilesEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberFilesEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberFilesEntity(PathMetadata metadata, PathInits inits) {
        this(MemberFilesEntity.class, metadata, inits);
    }

    public QMemberFilesEntity(Class<? extends MemberFilesEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new org.myweb.first.member.jpa.entity.QMemberEntity(forProperty("member"), inits.get("member")) : null;
    }

}

