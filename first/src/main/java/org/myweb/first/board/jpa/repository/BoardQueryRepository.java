package org.myweb.first.board.jpa.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.myweb.first.board.jpa.entity.BoardEntity;
import org.myweb.first.board.jpa.entity.BoardNativeVo;
import org.myweb.first.board.jpa.entity.QBoardEntity;
import org.myweb.first.board.model.dto.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardQueryRepository {
    private final JPAQueryFactory queryFactory;

    private final EntityManager entityManager;
    private QBoardEntity board = QBoardEntity.boardEntity;


    public List<BoardEntity> findTop3() {
        return queryFactory
                .selectFrom(board)
                .orderBy(board.boardReadCount.desc())
                .fetch();
    }

    public int findLastBoardNum() {
        BoardEntity boardEntity = queryFactory
                .selectFrom(board)
                .orderBy(board.boardNum.desc())
                .fetch().get(0);
        return boardEntity.getBoardNum();
    }

    //검색 관련 메소드 --------------------------------------------------
    public long countSearchTitle(String keyword) {
        return queryFactory
                .selectFrom(board)
                .where(board.boardTitle.like("%" + keyword + "%"))
                .orderBy(board.boardNum.desc())
                .fetchCount();
    }

    public long countSearchWriter(String keyword) {
        return queryFactory
                .selectFrom(board)
                .where(board.boardWriter.like("%" + keyword + "%"))
                .orderBy(board.boardNum.desc())
                .fetchCount();
    }

    public long countSearchDate(Date begin, Date end) {
        return queryFactory
                .selectFrom(board)
                .where(board.boardDate.between(begin, end))
                .orderBy(board.boardNum.desc())
                .fetchCount();
    }

    public List<BoardEntity> findSearchTitle(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(board)
                .where(board.boardTitle.like("%" + keyword + "%"))
                .orderBy(board.boardNum.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public List<BoardEntity> findSearchWriter(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(board)
                .where(board.boardWriter.like("%" + keyword + "%"))
                .orderBy(board.boardNum.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public List<BoardEntity> findSearchDate(Date begin, Date end, Pageable pageable) {
        return queryFactory
                .selectFrom(board)
                .where(board.boardDate.between(begin, end))
                .orderBy(board.boardNum.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

}//BoardQueryRepository end
