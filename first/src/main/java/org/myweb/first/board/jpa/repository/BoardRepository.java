package org.myweb.first.board.jpa.repository;

import org.myweb.first.board.jpa.entity.BoardEntity;
import org.myweb.first.board.jpa.entity.BoardNativeVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// Spring Data JPA 리포지토리에 QueryDSL을 통합하기 위해 QuerydslPredicateExecutor 인터페이스를 확장한다.
public interface BoardRepository extends JpaRepository<BoardEntity, Integer>, QuerydslPredicateExecutor<BoardEntity> {
    //해당 인터페이스가 비어 있으면, JpaRepository 가 제공하는 기본 메소드들을 사용한다는 의미임

//    //jpa 기본 메소드로 해결이 안되는 쿼리문 작동일 때는 필요한 메소드를 이 인터페이스 안에 추가할 수 있음
//    //JPQL 을 이용함 또는 Native Query 이용하면 됨
//    //JPQL 은 WHERE, GROUP BY 절에서만 서브쿼리 사용 가능함
//    //FROM 절에서는 서브쿼리 사용 못 함
//
//    //@Query + Native Query 사용 형태 (쿼리문에 테이블과 컬럼명 사용)  -----------------------------------
//    @Query(value = "select board_num, board_title, board_readcount from board order by board_readcount desc", nativeQuery = true)
//    List<BoardNativeVo> findTop3();
//    //nativeQuery 사용시 select 절의 컬럼명과 같은 get 메소드로만 구성된 nativeVo 인터페이스가 필요함
//    // board_num, board_title, board_readcount 컬럼에 대한 get 메소드 작성
//    // => board.jpa.entity.BoardNativeVo 인터페이스로 작성함
//
//    //insert 등록시 새 게시글 번호 처리를 위해서 마지막 게시글번호 조회용
//    @Query(value = "select max(board_num) from board", nativeQuery = true)
//    int findLastBoardNum();
//
//    //@Query + Native Query 사용 형태 (작성하는 쿼리문에 테이블과 컬럼명 사용)  -----------------------------------
//    @Query(value = "select count(*) from board b where b.board_title like %:keyword%", nativeQuery = true)
//    Long countSearchTitle(@Param("keyword") String keyword);
//
//    @Query(value = "select count(*) from board b where b.board_writer like %:keyword%", nativeQuery = true)
//    Long countSearchWriter(@Param("keyword") String keyword);
//
//    @Query(value = "select count(*) from board b where b.board_date between :begin and :end", nativeQuery = true)
//    Long countSearchDate(@Param("begin") java.sql.Date begin, @Param("end") java.sql.Date end);
//
//    //@Query + JPQL 사용 (작성하는 쿼리문에 엔티티 클래스명과 프로퍼티 사용) ---------------------
//    @Query(value = "select b from BoardEntity b where b.boardTitle like %:keyword%",
//            countQuery = "select count(b) from BoardEntity b where b.boardTitle like %:keyword%")
//    Page<BoardEntity> findSearchTitle(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);
//
//    @Query(value = "select b from BoardEntity b where b.boardWriter like %:keyword%",
//            countQuery = "select count(b) from BoardEntity b where b.boardWriter like %:keyword%")
//    Page<BoardEntity> findSearchWriter(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);
//
//    @Query(value = "select b from BoardEntity b where b.boardDate between :begin and :end",
//            countQuery = "select count(b) from BoardEntity b where b.boardDate between :begin and :end")
//    Page<BoardEntity> findSearchDate(@Param("begin") java.sql.Date begin, @Param("end") java.sql.Date end, @Param("pageable") Pageable pageable);
//
}
