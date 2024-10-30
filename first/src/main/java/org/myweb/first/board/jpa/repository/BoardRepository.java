package org.myweb.first.board.jpa.repository;

import org.myweb.first.board.jpa.entity.BoardEntity;
import org.myweb.first.board.jpa.entity.BoardNativeVo;
import org.myweb.first.board.model.dto.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {
    // 해당 인터페이스가 비어 있으면, JpaRepository가 제공하는 기본 메소드들을 사용한다는 의미임

    // JPA 기본 메소드로 해결이 안되는 쿼리문 작동일 때는 필요한 메소드를 이 인터페이스 안에 추가할 수 있음
    // JPQL을 이용함 또는 Native Query를 이용함
    // JPQL은 WHERE, GROUP BY 절에서만 서브쿼리 사용 가능함
    // FROM 절에서는 서비쿼리 사용 못 함



    //리포티지토리에서는 DTO 처리 못 함, 무조건 Entity로 쿼리를 이용해서 DB를 조회할 수 있음



    //@Query + Native Query 사용 형태 (쿼리문에 테이블과 칼럼명 사용)   ------------------------------------------------------------------
    @Query(value = "SELECT board_num, board_title, board_readcount from BOARD ORDER BY BOARD_READCOUNT DESC", nativeQuery= true)
    List<BoardNativeVo> findTop3();
    // nativeQuery 사용 시 칼럼명과 같은 get 메소드로만 구성된 nativeVo 인터페이스가 필요함
    // => board.jpa.entity.BoardNativeVo 인터페이스로 작성함


    // insert 등록 시 새 게시글 번호 처리를 위해서 마지막 게시글 번호 조회용
    @Query(value = "SELECT MAX(board_num) FROM BOARD", nativeQuery= true)  // insert 등록 시, 새 게시글 번호 처리를 위해서 마지막 게시글번호 조회용
    int findLastBoardNum();


    //@Query + Native Query 사용 형태 (쿼리문에 테이블과 컬럼명 사용) ----------------------------------------------------------------------
    @Query(value = "SELECT  count(*) FROM BOARD B WHERE B.BOARD_TITLE LIKE %:keyword%", nativeQuery= true)
    Long countSearchTitle(@Param("keyword") String title);


    //@Query + Native Query 사용 형태 (쿼리문에 테이블과 컬럼명 사용) ----------------------------------------------------------------------
    @Query(value = "SELECT  count(*) FROM BOARD B WHERE B.BOARD_WRITER LIKE %:keyword%", nativeQuery= true)
    Long countSearchWriter(@Param("keyword") String title);


    //@Query + Native Query 사용 형태 (쿼리문에 테이블과 컬럼명 사용) ----------------------------------------------------------------------
    @Query(value = "SELECT  count(*) FROM BOARD B WHERE B.BOARD_DATE BETWEEN :begin and :end", nativeQuery= true)
    Long countSearchDate(@Param("begin") java.sql.Date begin, @Param("end") java.sql.Date end);


    //@Query + JPQL 사용  (작성하는 쿼리문에 엔터티 클래스명과 프로터티를 사용) -----------------------------------------------------------
    @Query(value = "SELECT b FROM BoardEntity b WHERE b.boardTitle LIKE %:keyword% ORDER BY b.boardDate DESC",
            countQuery = "SELECT COUNT(b) FROM BoardEntity b WHERE b.boardTitle LIKE %:keyword%")
    Page<BoardEntity> findSearchTitle(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);

    //@Query + JPQL 사용  (작성하는 쿼리문에 엔터티 클래스명과 프로터티를 사용) -----------------------------------------------------------
    @Query(value = "SELECT b FROM BoardEntity b WHERE b.boardWriter LIKE %:keyword% ORDER BY b.boardDate DESC",
            countQuery = "SELECT COUNT(b) FROM BoardEntity b WHERE b.boardWriter LIKE %:keyword%")
    Page<BoardEntity> findSearchWriter(@Param("keyword") String keyword, @Param("pageable") Pageable pageable);

    @Query(value = "SELECT b FROM BoardEntity b WHERE b.boardDate BETWEEN :startDate AND :endDate ORDER BY b.boardDate DESC",
            countQuery = "SELECT COUNT(b) FROM BoardEntity b WHERE b.boardDate BETWEEN :startDate AND :endDate")
    Page<BoardEntity> findSearchDate(@Param("startDate") java.util.Date startDate,
                                     @Param("endDate") java.util.Date endDate,
                                     Pageable pageable);

}
