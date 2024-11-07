package org.myweb.first.reply.jpa.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.myweb.first.reply.jpa.entity.QReplyEntity;
import org.myweb.first.reply.jpa.entity.ReplyEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReplyQueryRepository {
    private final JPAQueryFactory queryFactory;

    private final EntityManager entityManager;
    private QReplyEntity reply = QReplyEntity.replyEntity;

    public List<ReplyEntity> findAllReply(int boardNum) {
        return queryFactory
                .selectFrom(reply)
                .where(reply.boardRef.eq(boardNum))
                .orderBy(reply.replyReplyRef.desc(), reply.replyLev.asc(), reply.replySeq.desc())
                .fetch();
    }

    public void addReadCount(int boardRef) {
        queryFactory
            .update(reply)
            .set(reply.replyReadCount, reply.replyReadCount.add(1))
            .where(reply.boardRef.eq(boardRef))
            .execute();
    }

    public int findLastReplyNum() {
        ReplyEntity replyEntity = queryFactory
                .selectFrom(reply)
                .orderBy(reply.replyNum.desc())
                .fetch().get(0);
        return replyEntity.getReplyNum();
    }

    public long countReplySeq(int boardRef, int replyLev) {
        return queryFactory
                .selectFrom(reply)
                .where(reply.boardRef.eq(boardRef), reply.replyLev.eq(replyLev))
                .fetchCount();
    }

    public int findLastReplySeq(int boardRef, int replyLev) {
        ReplyEntity replyEntity = queryFactory
                .selectFrom(reply)
                .where(reply.boardRef.eq(boardRef), reply.replyLev.eq(replyLev))
                .orderBy(reply.replySeq.desc())
                .fetch().get(0);
        return replyEntity.getReplySeq();
    }

    public long countReplyReplySeq(int boardRef, int replyReplyRef, int replyLev) {
        return queryFactory
                .selectFrom(reply)
                .where(reply.boardRef.eq(boardRef), reply.replyReplyRef.eq(replyReplyRef), reply.replyLev.eq(replyLev))
                .fetchCount();
    }

    public int findLastReplyReplySeq(int boardRef, int replyReplyRef, int replyLev) {
        ReplyEntity replyEntity = queryFactory
                .selectFrom(reply)
                .where(reply.boardRef.eq(boardRef), reply.replyReplyRef.eq(replyReplyRef), reply.replyLev.eq(replyLev))
                .orderBy(reply.replySeq.desc())
                .fetch().get(0);
        return replyEntity.getReplySeq();
    }

}//BoardQueryRepository end
