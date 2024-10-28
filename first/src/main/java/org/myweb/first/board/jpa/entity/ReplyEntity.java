package org.myweb.first.board.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "REPLY")
public class ReplyEntity {
    @Id
    @Column(name = "BOARD_REF" , nullable = false)
    private int boardRef;		//BOARD_REF	NUMBER : 참조하는 원글 번호 (원글일 때는 자기번호)
    @Column(name = "BOARD_REPLY_REF", nullable = false)
    private int boardReplyRef;	//BOARD_REPLY_REF	NUMBER : 참조하는 댓글번호
    //(원글 : NULL, 댓글: 자기번호, 대댓글 : 참조하는 댓글번호)
    @Column(name = "BOARD_LEV", nullable = false)
    private int boardLev;		//BOARD_LEV	NUMBER : 원글 : 1, 원글의 댓글 : 2, 댓글의 댓글(대댓글) : 3
    @Column(name = "BOARD_REPLY_SEQ", nullable = false)
    private int boardReplySeq;	//BOARD_REPLY_SEQ	NUMBER : 댓글의 순번 (최근 댓글 | 대댓글을 1로 할 것임)
}
