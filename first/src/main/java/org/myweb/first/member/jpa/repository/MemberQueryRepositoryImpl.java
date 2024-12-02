//package org.myweb.first.member.jpa.repository;
//
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import org.myweb.first.member.jpa.entity.MemberEntity;
//import org.myweb.first.member.jpa.entity.QMemberEntity;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//import org.springframework.util.StringUtils;
//
//import java.sql.Date;
//import java.util.List;
//
//@Repository
//public class MemberQueryRepositoryImpl implements MemberQueryRepository {
//    private final JPAQueryFactory queryFactory;
//    private final QMemberEntity member = QMemberEntity.memberEntity;
//
//    public MemberQueryRepositoryImpl(JPAQueryFactory queryFactory) {
//        this.queryFactory = queryFactory;
//    }
//
//    // Helper 메소드: 회원 ID로 검색하는 조건 생성
//    private BooleanExpression userIdContains(String keyword) {
//        return StringUtils.hasText(keyword) ? member.userId.containsIgnoreCase(keyword) : null;
//    }
//
//    // Helper 메소드: 성별로 검색하는 조건 생성
//    private BooleanExpression genderContains(String keyword) {
//        return StringUtils.hasText(keyword) ? member.gender.containsIgnoreCase(keyword) : null;
//    }
//
//    // Helper 메소드: 나이 범위 조건 생성
//    private BooleanExpression ageBetween(int age) {
//        return member.age.between(age, age + 9);
//    }
//
//    // Helper 메소드: 가입일 범위 조건 생성
//    private BooleanExpression enrollDateBetween(Date begin, Date end) {
//        return begin != null && end != null ? member.enrollDate.between(begin, end) : null;
//    }
//
//    // Helper 메소드: 로그인 상태로 검색하는 조건 생성
//    private BooleanExpression loginOkContains(String keyword) {
//        return StringUtils.hasText(keyword) ? member.loginOk.containsIgnoreCase(keyword) : null;
//    }
//
//    @Override
//    public Page<MemberEntity> searchMembers(String action, String keyword, Date begin, Date end, Pageable pageable) {
//        BooleanExpression condition = getCondition(action, keyword, begin, end);
//
//        List<MemberEntity> results = queryFactory
//                .selectFrom(member)
//                .where(condition)
//                .orderBy(member.userId.asc()) // 사용자 ID 오름차순 정렬
//                .offset(pageable.getOffset()) // 페이징 오프셋
//                .limit(pageable.getPageSize()) // 페이징 제한
//                .fetch();
//
//        // 전체 결과 수 조회
//        long total = queryFactory
//                .selectFrom(member)
//                .where(condition)
//                .fetch().size();
//
//        // Page<MemberEntity> 객체 생성
//        return new PageImpl<>(results, pageable, total); // 페이징된 결과 반환
//    }
//
//    // 검색 조건을 동적으로 생성
//    private BooleanExpression getCondition(String action, String keyword, Date begin, Date end) {
//        switch (action) {
//            case "id":
//                return userIdContains(keyword);
//            case "gender":
//                return genderContains(keyword);
//            case "age":
//                try {
//                    return ageBetween(Integer.parseInt(keyword));
//                } catch (NumberFormatException e) {
//                    return null; // 숫자 변환 실패 시 조건 없음
//                }
//            case "enrollDate":
//                return enrollDateBetween(begin, end);
//            case "loginok":
//                return loginOkContains(keyword);
//            default:
//                return null;
//        }
//    }
//}
