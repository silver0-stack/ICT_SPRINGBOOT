//package org.myweb.first.member.jpa.repository;
//
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import jakarta.persistence.EntityManager;
//import lombok.RequiredArgsConstructor;
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
//
//
///*
//* 회원 검색 관련 쿼리를 처리하는 리포지토리 클래스
//* QueryDSL을 사용하여 동적 쿼리 실행
//* */
//@Repository
//public interface MemberQueryRepository {
//    /**
//     * 역할별 검색 메소드
//     *
//     * @param keyword 검색 키워드
//     * @param categories 허용된 검색 카테고리 목록
//     * @param pageable 페이징 및 정렬 정보
//     * @return 페이징된 검색 결과
//     */
//    Page<Object> search(String keyword, List<String> categories, Pageable pageable);
//}
//}//MemberQueryRepository end
