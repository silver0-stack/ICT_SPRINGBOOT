package org.myweb.first.board.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.board.jpa.entity.ReplyEntity;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reply {
    private int boardRef;        //BOARD_REF	NUMBER : 참조하는 원글 번호 (원글일 때는 자기번호)
    private int boardReplyRef;    //BOARD_REPLY_REF	NUMBER : 참조하는 댓글번호
    //(원글 : NULL, 댓글: 자기번호, 대댓글 : 참조하는 댓글번호)
    private int boardLev;        //BOARD_LEV	NUMBER : 원글 : 1, 원글의 댓글 : 2, 댓글의 댓글(대댓글) : 3
    private int boardReplySeq;    //BOARD_REPLY_SEQ	NUMBER : 댓글의 순번 (최근 댓글 | 대댓글을 1로 할 것임)
    public int replyNum;        //REPLY_NUM	NUMBER	No
    public String replyWriter;      //REPLY_WRITER	VARCHAR2(50 BYTE)	No
    public String replyTitle;       //REPLY_TITLE	VARCHAR2(50 BYTE)	No
    public String replyContent;              //REPLY_CONTENT	VARCHAR2(2000 BYTE)	No
    public int replyReplyRef;           //REPLY_REPLY_REF	NUMBER	Yes
    public int replyLev;             //REPLY_LEV	NUMBER	Yes	1
    public int replySeq;            //REPLY_SEQ	NUMBER	Yes	1
    public int replyReadCount;          //REPLY_READCOUNT	NUMBER	Yes	0
    public Date replyDate;          //REPLY_DATE	DATE	Yes	SYSDATE

    // DTO -> Entity
    public ReplyEntity toEntity() {
        return ReplyEntity.builder()
                .boardRef(boardRef)
                .boardReplyRef(boardReplyRef)
                .boardLev(boardLev)
                .boardReplySeq(boardReplySeq)
                .replyNum(replyNum)
                .replyWriter(replyWriter)
                .replyTitle(replyTitle)
                .replyContent(replyContent)
                .replyReplyRef(replyReplyRef)
                .replyLev(replyLev)
                .replySeq(replySeq)
                .replyReadCount(replyReadCount)
                .replyDate(replyDate)
                .build();

    }
}
