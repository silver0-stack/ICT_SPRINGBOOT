package org.myweb.first.board.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.board.jpa.entity.ReplyEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reply {
    private int boardRef;		//BOARD_REF	NUMBER : 참조하는 원글 번호 (원글일 때는 자기번호)
    private int boardReplyRef;	//BOARD_REPLY_REF	NUMBER : 참조하는 댓글번호
    //(원글 : NULL, 댓글: 자기번호, 대댓글 : 참조하는 댓글번호)
    private int boardLev;		//BOARD_LEV	NUMBER : 원글 : 1, 원글의 댓글 : 2, 댓글의 댓글(대댓글) : 3
    private int boardReplySeq;	//BOARD_REPLY_SEQ	NUMBER : 댓글의 순번 (최근 댓글 | 대댓글을 1로 할 것임)

    // DTO -> Entity
    public ReplyEntity toEntity() {
        return ReplyEntity.builder()
                .boardRef(boardRef)
                .boardReplyRef(boardReplyRef)
                .boardLev(boardLev)
                .boardReplySeq(boardReplySeq)
                .build();

    }
}
