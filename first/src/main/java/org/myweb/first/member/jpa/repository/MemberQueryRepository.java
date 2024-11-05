package org.myweb.first.member.jpa.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.myweb.first.member.jpa.entity.MemberEntity;
import org.myweb.first.member.jpa.entity.QMemberEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {
    private final JPAQueryFactory queryFactory;

    private final EntityManager entityManager;
    private QMemberEntity member = QMemberEntity.memberEntity;


    public long countSearchUserId(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.userId.like("%" + keyword + "%"))
                .orderBy(member.userId.asc())
                .fetchCount();
    }

    public long countSearchGender(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.gender.like("%" + keyword + "%"))
                .orderBy(member.userId.asc())
                .fetchCount();
    }

    public long countSearchAge(int age) {
        return queryFactory
                .selectFrom(member)
                .where(member.age.between(age, age+9))
                .orderBy(member.userId.asc())
                .fetchCount();
    }

    public long countSearchDate(Date begin, Date end) {
        return queryFactory
                .selectFrom(member)
                .where(member.enrollDate.between(begin, end))
                .orderBy(member.userId.asc())
                .fetchCount();
    }

    public long countSearchLoginOK(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.loginOk.like("%" + keyword + "%"))
                .orderBy(member.userId.asc())
                .fetchCount();
    }

    public List<MemberEntity> findSearchUserId(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(member)
                .where(member.userId.like("%" + keyword + "%"))
                .orderBy(member.userId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public List<MemberEntity> findSearchGender(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(member)
                .where(member.gender.like("%" + keyword + "%"))
                .orderBy(member.userId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public List<MemberEntity> findSearchAge(int age, Pageable pageable) {
        return queryFactory
                .selectFrom(member)
                .where(member.age.between(age, age+9))
                .orderBy(member.userId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public List<MemberEntity> findSearchDate(Date begin, Date end, Pageable pageable) {
        return queryFactory
                .selectFrom(member)
                .where(member.enrollDate.between(begin, end))
                .orderBy(member.userId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public List<MemberEntity> findSearchLoginOK(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(member)
                .where(member.loginOk.like("%" + keyword + "%"))
                .orderBy(member.userId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

}//MemberQueryRepository end
