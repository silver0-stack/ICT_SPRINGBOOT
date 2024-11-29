package org.myweb.first.member.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberEntity is a Querydsl query type for MemberEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberEntity extends EntityPathBase<MemberEntity> {

    private static final long serialVersionUID = -1783332401L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberEntity memberEntity = new QMemberEntity("memberEntity");

    public final StringPath memAddress = createString("memAddress");

    public final StringPath memCellphone = createString("memCellphone");

    public final DateTimePath<java.sql.Timestamp> memChangeStatus = createDateTime("memChangeStatus", java.sql.Timestamp.class);

    public final StringPath memEmail = createString("memEmail");

    public final DateTimePath<java.sql.Timestamp> memEnrollDate = createDateTime("memEnrollDate", java.sql.Timestamp.class);

    public final StringPath memFamilyApproval = createString("memFamilyApproval");

    public final StringPath memGoogleEmail = createString("memGoogleEmail");

    public final StringPath memGovCode = createString("memGovCode");

    public final StringPath memId = createString("memId");

    public final StringPath memKakaoEmail = createString("memKakaoEmail");

    public final StringPath memName = createString("memName");

    public final StringPath memNaverEmail = createString("memNaverEmail");

    public final StringPath memPhone = createString("memPhone");

    public final StringPath memPw = createString("memPw");

    public final StringPath memRnn = createString("memRnn");

    public final StringPath memSocialGoogle = createString("memSocialGoogle");

    public final StringPath memSocialKakao = createString("memSocialKakao");

    public final StringPath memSocialNaver = createString("memSocialNaver");

    public final StringPath memStatus = createString("memStatus");

    public final StringPath memType = createString("memType");

    public final StringPath memUuid = createString("memUuid");

    public final StringPath memUuidFam = createString("memUuidFam");

    public final StringPath memUuidMgr = createString("memUuidMgr");

    public final org.myweb.first.files.member.jpa.entity.QMemberFilesEntity profilePicture;

    public QMemberEntity(String variable) {
        this(MemberEntity.class, forVariable(variable), INITS);
    }

    public QMemberEntity(Path<? extends MemberEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberEntity(PathMetadata metadata, PathInits inits) {
        this(MemberEntity.class, metadata, inits);
    }

    public QMemberEntity(Class<? extends MemberEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profilePicture = inits.isInitialized("profilePicture") ? new org.myweb.first.files.member.jpa.entity.QMemberFilesEntity(forProperty("profilePicture"), inits.get("profilePicture")) : null;
    }

}

