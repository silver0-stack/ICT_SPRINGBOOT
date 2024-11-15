package org.myweb.first.member.jpa.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.myweb.first.member.jpa.entity.MemberEntity;
import org.myweb.first.member.jpa.entity.QMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.util.List;



/*
* 회원 검색 관련 쿼리를 처리하는 리포지토리 클래스
* QueryDSL을 사용하여 동적 쿼리 실행
* */
@Repository
@RequiredArgsConstructor // final 필드를 인자로 받는 생성자 생성
public class MemberQueryRepository {
    private final JPAQueryFactory queryFactory; // QueryDSL을 위한 JPAQueryFactory

    private final QMemberEntity member = QMemberEntity.memberEntity; // QueryDSL Q 클래스 인스턴스

    /*
    조건을 생성하는 BooleanExpression 메소드를 추가하여 코드의 재사용성과 가독성을 향상시킴
    각 검색 조건에 대해 null 체크를 포함하여 동적 쿼리를 보다 안전하게 작성함

    각 메소드에서 동일한 패턴의 코드를 사용하므로, Helper 메소드를 통해 중복을 줄임
    */


    // Helper 메소드: 회원 ID로 검색하는 조건 생성
    private BooleanExpression userIdContains(String keyword) {
        return StringUtils.hasText(keyword) ? member.userId.containsIgnoreCase(keyword) : null;
    }

    // Helper 메소드: 성별로 검색하는 조건 생성
    private BooleanExpression genderContains(String keyword) {
        return StringUtils.hasText(keyword) ? member.gender.containsIgnoreCase(keyword) : null;
    }

    // Helper 메소드: 나이 범위 조건 생성
    private BooleanExpression ageBetween(int age) {
        return member.age.between(age, age + 9);
    }

    // Helper 메소드: 가입일 범위 조건 생성
    private BooleanExpression enrollDateBetween(Date begin, Date end) {
        return begin != null && end != null ? member.enrollDate.between(begin, end) : null;
    }

    // Helper 메소드: 로그인 상태로 검색하는 조건 생성
    private BooleanExpression loginOkContains(String keyword) {
        return StringUtils.hasText(keyword) ? member.loginOk.containsIgnoreCase(keyword) : null;
    }


    /*
    * 조건별 회원 검색 메소드
    * @param action 검색 기준(id, gender, age, enrollDatem loginOk)
    * @param keyword 검색 키워드
    * @param begin 검색 시작 날짜 (enrollDate 기준)
    * @param end 검색 종료 날짜 (enrollDate 기준)
    * @param pageable 페이징 및 정렬 정보
    * @return 페이징된 회원 엔터티 페이지
    * */
    public Page<MemberEntity> searchMembers(String action, String keyword, Date begin, Date end, Pageable pageable) {
        BooleanExpression condition = getCondition(action, keyword, begin, end); // 검색 조건 생성

        // 회원 목록 조회
        List<MemberEntity> results = queryFactory
                .selectFrom(member)
                .where(condition)
                .orderBy(member.userId.asc()) // 사용자 ID 오름차순 정렬
                .offset(pageable.getOffset()) // 페이징 오프셋
                .limit(pageable.getPageSize()) // 페이징 제한
                .fetch();

        // 전체 결과 수 조회
        long total = queryFactory
                .selectFrom(member)
                .where(condition)
                .fetch().size();

        return new PageImpl<>(results, pageable, total); //페이징된 결과 반환
    }


    private BooleanExpression getCondition(String action, String keyword, Date begin, Date end) {
        return switch (action) {
            case "id" -> userIdContains(keyword);
            case "gender" -> genderContains(keyword);
            case "age" -> ageBetween(Integer.parseInt(keyword));
            case "enrollDate" -> enrollDateBetween(begin, end);
            case "loginok" -> loginOkContains(keyword);
            default -> null;
        };
    }

    // 조건 생성
    // 회원 ID로 검색한 결과의 갯수
//    public long countSearchUserId(String keyword) {
//        return queryFactory
//                .selectFrom(member)
//                .where(userIdContains(keyword))
//                .fetch().size();
//    }
//
//    // 성별로 검색한 결과의 개수
//    public long countSearchGender(String keyword) {
//        return queryFactory
//                .selectFrom(member)
//                .where(genderContains(keyword))
//                .fetch().size();
//    }
//
//    // 나이로 검색한 결과의 개수
//    public long countSearchAge(int age) {
//        return queryFactory
//                .selectFrom(member)
//                .where(ageBetween(age))
//                .fetch().size();
//    }
//
//    // 가입일로 검색한 결과의 개수
//    public long countSearchDate(Date begin, Date end) {
//        return queryFactory
//                .selectFrom(member)
//                .where(enrollDateBetween(begin, end))
//                .fetch().size();
//    }
//
//    // 로그인 상태로 검색한 결과의 개수
//    public long countSearchLoginOK(String keyword) {
//        return queryFactory
//                .selectFrom(member)
//                .where(loginOkContains(keyword))
//                .fetch().size();
//    }
//
//    // 회원 ID로 검색한 회원 목록 조회
//    public List<MemberEntity> findSearchUserId(String keyword, Pageable pageable) {
//        return queryFactory
//                .selectFrom(member)
//                .where(userIdContains(keyword))
//                .orderBy(member.userId.asc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//    }
//
//    // 성별로 검색한 회원 목록 조회
//    public List<MemberEntity> findSearchGender(String keyword, Pageable pageable) {
//        return queryFactory
//                .selectFrom(member)
//                .where(genderContains(keyword))
//                .orderBy(member.userId.asc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//    }
//
//    // 나이로 검색한 회원 목록 조회
//    public List<MemberEntity> findSearchAge(int age, Pageable pageable) {
//        return queryFactory
//                .selectFrom(member)
//                .where(ageBetween(age))
//                .orderBy(member.userId.asc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//    }
//
//    // 가입일로 검색한 회원 목록 조회
//    public List<MemberEntity> findSearchDate(Date begin, Date end, Pageable pageable) {
//        return queryFactory
//                .selectFrom(member)
//                .where(enrollDateBetween(begin, end))
//                .orderBy(member.userId.asc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//    }
//
//    // 로그인 상태로 검색한 회원 목록 조회
//    public List<MemberEntity> findSearchLoginOK(String keyword, Pageable pageable) {
//        return queryFactory
//                .selectFrom(member)
//                .where(loginOkContains(keyword))
//                .orderBy(member.userId.asc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//    }

}//MemberQueryRepository end
