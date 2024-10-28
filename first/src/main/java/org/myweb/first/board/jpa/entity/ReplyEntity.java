package org.myweb.first.board.jpa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "REPLY")
public class ReplyEntity {
    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reply_seq_generator")
    @SequenceGenerator(name = "reply_seq_generator", sequenceName = "REPLY_SEQ", allocationSize = 1)
    @Column(name = "REPLY_NUM", nullable = false)
    private Integer replyNum; // REPLY_NUM NUMBER NOT NULL

    // Foreign Keys and References
    @Column(name = "BOARD_REF", nullable = false)
    private Integer boardRef; // BOARD_REF NUMBER NOT NULL

    @Column(name = "BOARD_REPLY_REF")
    private Integer boardReplyRef; // BOARD_REPLY_REF NUMBER

    @Column(name = "BOARD_LEV", nullable = false)
    private Integer boardLev; // BOARD_LEV NUMBER NOT NULL

    @Column(name = "BOARD_REPLY_SEQ", nullable = false)
    private Integer boardReplySeq; // BOARD_REPLY_SEQ NUMBER NOT NULL

    // Reply Details
    @Column(name = "REPLY_WRITER", nullable = false, length = 50)
    private String replyWriter; // REPLY_WRITER VARCHAR2(50 BYTE) NOT NULL

    @Column(name = "REPLY_TITLE", nullable = false, length = 50)
    private String replyTitle; // REPLY_TITLE VARCHAR2(50 BYTE) NOT NULL

    @Column(name = "REPLY_CONTENT", nullable = false, length = 2000)
    private String replyContent; // REPLY_CONTENT VARCHAR2(2000 BYTE) NOT NULL

    @Column(name = "REPLY_REPLY_REF")
    private Integer replyReplyRef; // REPLY_REPLY_REF NUMBER

    @Column(name = "REPLY_LEV", nullable = false)
    private Integer replyLev = 1; // REPLY_LEV NUMBER NOT NULL, 기본값 1

    @Column(name = "REPLY_SEQ", nullable = false)
    private Integer replySeq = 1; // REPLY_SEQ NUMBER NOT NULL, 기본값 1

    @Column(name = "REPLY_READCOUNT", nullable = false)
    private Integer replyReadCount = 0; // REPLY_READCOUNT NUMBER NOT NULL, 기본값 0

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "REPLY_DATE", nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Date replyDate; // REPLY_DATE DATE NOT NULL, 기본값 SYSDATE
}
