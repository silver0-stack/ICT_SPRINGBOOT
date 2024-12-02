package org.myweb.first.member.jpa.repository;

import org.myweb.first.member.jpa.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * 회원 엔티티에 대한 JPA 리포지토리 인터페이스
 * CRUD, 페이징, 정렬 등의 기본 기능을 제공하며, 복잡한 쿼리는 MemberQueryRepository에서 처리
 */
@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String>{
    // MEM_ID로 회원을 찾는 메소드
    Optional<MemberEntity> findByMemId(String memId);
    // MEM_UUID로 회원을 찾는 메소드
    Optional<MemberEntity> findByMemUuid(String memUuid);

    // 사용자 ID로 검색한 결과의 개수(Native Query 사용)
//    @Query(value = "select count(*) from member m where m.userid like %:keyword%", nativeQuery = true)
//    Long countSearchUserId(@Param("keyword") String keyword);
//
//    // 성별로 검색한 결과의 개수(Native Query 사용)
//    @Query(value = "select count(*) from member m where m.gender like %:keyword%", nativeQuery = true)
//    Long countSearchGender(@Param("keyword") String keyword);
//
//    // 나이로 검색한 결과의 개수 (Native Query 사용)
//    @Query(value = "select count(*) from member m where m.age between :age and :age + 9", nativeQuery = true)
//    Long countSearchAge(@Param("age") int age);
//
//    // 가입일로 검색한 결과의 개수 (Native Query 사용)
//    @Query(value = "select count(*) from member m where m.enroll_date between :begin and :end", nativeQuery = true)
//    Long countSearchDate(@Param("begin") java.sql.Date begin, @Param("end") java.sql.Date end);
//
//    // 로그인 상태로 검색한 결과의 개수 (Native Query 사용)
//    @Query(value = "select count(*) from member m where m.login_ok like %:keyword%", nativeQuery = true)
//    Long countSearchLoginOK(@Param("keyword") String keyword);
//
//
//    // 사용자 ID로 검색한 회원 목록 조회 (JPQL 사용)
//    @Query(value = "select m from MemberEntity m where m.userId like %:keyword%",
//            countQuery = "select count(m) from MemberEntity m where m.userId like %:keyword%")
//    Page<MemberEntity> findSearchUserId(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);
//
//    // 성별로 검색한 결과의 개수 (JPQL 사용)
//    @Query(value = "select m from MemberEntity m where m.gender like %:keyword%",
//            countQuery = "select count(m) from MemberEntity m where m.gender like %:keyword%")
//    Page<MemberEntity> findSearchGender(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);
//
//    // 나이로 검색한 회원 목록 조회 (JPQL 사용)
//    @Query(value = "select m from MemberEntity m where m.age between :age and :age + 9",
//            countQuery = "select count(m) from MemberEntity m where m.age between :age and :age + 9")
//    Page<MemberEntity> findSearchAge(@Param("age") int age, @Param("pageable") Pageable pageable);
//
//    // 가입일로 검색한 회원 목록 조회(JPQL 사용)
//    @Query(value = "select m from MemberEntity m where m.enrollDate between :begin and :end",
//            countQuery = "select count(m) from MemberEntity m where m.enrollDate between :begin and :end")
//    Page<MemberEntity> findSearchDate(@Param("begin") java.sql.Date begin, @Param("end") java.sql.Date end, @Param("pageable") Pageable pageable);
//
//    // 로그인 상태로 검색한 회원 목록 조회 (JPQL 사용)
//    @Query(value = "select m from MemberEntity m where m.loginOk like %:keyword%",
//            countQuery = "select count(m) from MemberEntity m where m.loginOk like %:keyword%")
//    Page<MemberEntity> findSearchLoginOK(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);
}
