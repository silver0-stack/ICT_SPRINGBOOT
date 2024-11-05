package org.myweb.first.member.jpa.repository;

import org.myweb.first.board.jpa.entity.BoardEntity;
import org.myweb.first.member.jpa.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String> {
    // jpa 가 제공하는 기본 메소드들을 사용할 수 있게 됨

//    //검색 관련 메소드 추가
//    //@Query + Native Query 사용 형태 (작성하는 쿼리문에 테이블과 컬럼명 사용)  -----------------------------------
//    @Query(value = "select count(*) from member m where m.userid like %:keyword%", nativeQuery = true)
//    Long countSearchUserId(@Param("keyword") String keyword);
//
//    @Query(value = "select count(*) from member m where m.gender like %:keyword%", nativeQuery = true)
//    Long countSearchGender(@Param("keyword") String keyword);
//
//    @Query(value = "select count(*) from member m where m.age between :age and :age + 9", nativeQuery = true)
//    Long countSearchAge(@Param("age") int age);
//
//    @Query(value = "select count(*) from member m where m.enroll_date between :begin and :end", nativeQuery = true)
//    Long countSearchDate(@Param("begin") java.sql.Date begin, @Param("end") java.sql.Date end);
//
//    @Query(value = "select count(*) from member m where m.login_ok like %:keyword%", nativeQuery = true)
//    Long countSearchLoginOK(@Param("keyword") String keyword);
//
//    //@Query + JPQL 사용 (작성하는 쿼리문에 엔티티 클래스명과 프로퍼티 사용) ---------------------
//    @Query(value = "select m from MemberEntity m where m.userId like %:keyword%",
//            countQuery = "select count(m) from MemberEntity m where m.userId like %:keyword%")
//    Page<MemberEntity> findSearchUserId(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);
//
//    @Query(value = "select m from MemberEntity m where m.gender like %:keyword%",
//            countQuery = "select count(m) from MemberEntity m where m.gender like %:keyword%")
//    Page<MemberEntity> findSearchGender(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);
//
//    @Query(value = "select m from MemberEntity m where m.age between :age and :age + 9",
//            countQuery = "select count(m) from MemberEntity m where m.age between :age and :age + 9")
//    Page<MemberEntity> findSearchAge(@Param("age") int age, @Param("pageable") Pageable pageable);
//
//    @Query(value = "select m from MemberEntity m where m.enrollDate between :begin and :end",
//            countQuery = "select count(m) from MemberEntity m where m.enrollDate between :begin and :end")
//    Page<MemberEntity> findSearchDate(@Param("begin") java.sql.Date begin, @Param("end") java.sql.Date end, @Param("pageable") Pageable pageable);
//
//    @Query(value = "select m from MemberEntity m where m.loginOk like %:keyword%",
//            countQuery = "select count(m) from MemberEntity m where m.loginOk like %:keyword%")
//    Page<MemberEntity> findSearchLoginOK(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);
}
