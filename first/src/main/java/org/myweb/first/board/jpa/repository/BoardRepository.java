package org.myweb.first.board.jpa.repository;

import org.myweb.first.board.jpa.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {
    // 해당 인터페이스가 비어 있으면, JpaRepository가 제공하는 기본 메소드들을 사용한다는 의미임

    // JPA 기본 메소드로 해결이 안되는 쿼리문 작동일 때는 필요한 메소드를 이 인터페이스 안에 추가할 수 있음
    // JPQL을 이용함

    //리포티지토리에서는 DTO 처리 못 함, 무조건 Entity로 쿼리를 이용해서 DB를 조회할 수 있음



    //@Query + Native Query 사용 형태 (쿼리문에 테이블과 칼럼명 사용)
    @Query(value = "SELECT board_num, board_title, board_readcount form BOARD ORDER BY BOARD_READCOUNT DESC", nativeQuery= true)
    List<BoardEntity> findTop3();
    // nativeQuery 사용 시 칼럼명과 같은 메소드로만



}
