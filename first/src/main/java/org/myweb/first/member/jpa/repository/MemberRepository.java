package org.myweb.first.member.jpa.repository;

import org.myweb.first.board.jpa.entity.BoardEntity;
import org.myweb.first.member.jpa.entity.MemberEntity;
import org.myweb.first.member.model.dto.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/*회원 엔터티에 대한 JPA 리포지토리 인터페이스
* 기본적인 CURD 작업을 제공하며, 복잡한 쿼리는 MemberQueryRepository에서 처리*/
@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String>, QuerydslPredicateExecutor<MemberEntity> {
    // 이제 @Query 메소드가 제거되었습니다
    // 기본적인 CRUD 작업은 JpaRepository 에서 처리됩니다
    // 복잡한 쿼리는 MemberQueryRepository 에서 처리합니다.


    // 검색 관련 메소드 추가
    // @Query + Native Query 사용 형태 (작성하는 쿼리문에 테이블과 컬럼명 사용)  -----------------------------------
    @Query(value = "select count(*) from member m where m.userid like %:keyword%", nativeQuery = true)
    Long countSearchUserId(@Param("keyword") String keyword);

    @Query(value = "select count(*) from member m where m.gender like %:keyword%", nativeQuery = true)
    Long countSearchGender(@Param("keyword") String keyword);

    @Query(value = "select count(*) from member m where m.age between :age and :age + 9", nativeQuery = true)
    Long countSearchAge(@Param("age") int age);

    @Query(value = "select count(*) from member m where m.enroll_date between :begin and :end", nativeQuery = true)
    Long countSearchDate(@Param("begin") java.sql.Date begin, @Param("end") java.sql.Date end);

    @Query(value = "select count(*) from member m where m.login_ok like %:keyword%", nativeQuery = true)
    Long countSearchLoginOK(@Param("keyword") String keyword);

    // @Query + JPQL 사용 (작성하는 쿼리문에 엔티티 클래스명과 프로퍼티 사용) ---------------------
    @Query(value = "select m from MemberEntity m where m.userId like %:keyword%",
            countQuery = "select count(m) from MemberEntity m where m.userId like %:keyword%")
    Page<MemberEntity> findSearchUserId(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);

    @Query(value = "select m from MemberEntity m where m.gender like %:keyword%",
            countQuery = "select count(m) from MemberEntity m where m.gender like %:keyword%")
    Page<MemberEntity> findSearchGender(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);

    @Query(value = "select m from MemberEntity m where m.age between :age and :age + 9",
            countQuery = "select count(m) from MemberEntity m where m.age between :age and :age + 9")
    Page<MemberEntity> findSearchAge(@Param("age") int age, @Param("pageable") Pageable pageable);

    @Query(value = "select m from MemberEntity m where m.enrollDate between :begin and :end",
            countQuery = "select count(m) from MemberEntity m where m.enrollDate between :begin and :end")
    Page<MemberEntity> findSearchDate(@Param("begin") java.sql.Date begin, @Param("end") java.sql.Date end, @Param("pageable") Pageable pageable);

    @Query(value = "select m from MemberEntity m where m.loginOk like %:keyword%",
            countQuery = "select count(m) from MemberEntity m where m.loginOk like %:keyword%")
    Page<MemberEntity> findSearchLoginOK(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);
}
