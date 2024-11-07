package org.myweb.first.reply.jpa.repository;

import org.myweb.first.reply.jpa.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {

//    /// @Query + Native Query 사용 (테이블명과 컬럼명 사용)
//    @Query(value = "select * from reply r where r.board_ref = :boardNum order by  r.reply_reply_ref desc, r.reply_lev asc, r.reply_seq desc", nativeQuery = true)
//    List<ReplyEntity> findAllReply(@Param("boardNum") int boardNum);
//    //@Param("전달이름") 자료형 변수명 : 엔티티는 사용할 수 없음, 실제 쿼리에 적용할 값으로 전달해야 함
//
//    //게시원글이 같은 댓글&대댓글 조회수 1증가 처리
//    //ORA-01002 에러 발생하면, 아래 2개 어노테이션을 추가함
//    //여러 행의 값을 수정할 때 커서와 트랜잭션 간의 문제로 발생하는 에러임
//    @Transactional
//    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Query(value = "update reply r set r.reply_readcount = r.reply_readcount + 1  where r.board_ref = :boardRef", nativeQuery = true)
//    void addReadCount(@Param("boardRef") int boardRef);
//
//    //새 댓글 또는 대댓글 등록을 위해 마지막 댓글번호 조회용
//    @Query(value = "select max(reply_num) from reply", nativeQuery = true)
//    Integer findLastReplyNum();
//
//    //댓글 갯수 조회용 : 같은 원글 & 같은 레벨의 댓글 갯수 카운트
//    @Query(value = "select count(reply_seq) from reply r where r.board_ref = :boardRef and r.reply_lev = :replyLev", nativeQuery = true)
//    Integer countReplySeq(int boardRef, int replyLev);
//
//    //댓글의 순번 기록을 위해 같은 원글, 같은 레벨의 마지막 순번 조회용
//    @Query(value = "select max(reply_seq) from reply r where r.board_ref = :boardRef and r.reply_lev = :replyLev", nativeQuery = true)
//    Integer findLastReplySeq(int boardRef, int replyLev);
//
//    //대댓글 갯수 조회용 : 같은 원글 & 같은 레벨의 댓글 갯수 카운트
//    @Query(value = "select count(reply_seq) from reply r where r.board_ref = :boardRef and r.reply_reply_ref = :replyReplyRef and r.reply_lev = :replyLev", nativeQuery = true)
//    Integer countReplyReplySeq(int boardRef, int replyReplyRef, int replyLev);
//
//    //대댓글의 순번 기록을 위해 같은 원글 & 같은 댓글 & 같은 레벨의 마지막 순번 조회용
//    @Query(value = "select max(reply_seq) from reply r where r.board_ref = :boardRef and r.reply_reply_ref = :replyReplyRef and r.reply_lev = :replyLev", nativeQuery = true)
//    Integer findLastReplyReplySeq(int boardRef, int replyReplyRef, int replyLev);
//
//    @Query(value = "select max(reply_seq) from reply where board_ref = :boardRef and reply_reply_ref = :replyReplyRef and reply_lev = :replyLev", nativeQuery = true)
//    Integer findLastReplySeq(@Param("boardRef") int boardRef, @Param("replyLev") int replyLev, @Param("replyReplyRef") int replyReplyRef);
//
}
