package org.myweb.first.board.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.board.jpa.entity.BoardEntity;

import java.util.Date;

@Data //@Getter, @Setter, @ToString, @AllArgsConstructor, @NoArgsConstructor, @Builder
@AllArgsConstructor
@NoArgsConstructor
@Builder //클래스 객체생성할 때 Builder.builder()로 생성할 수 있도록 @Builder 어노테이션
public class Board {
    private int boardNum;            //BOARD_NUM	NUMBER : 게시글 번호 (PK)
    private String boardWriter;        //BOARD_WRITER	VARCHAR2(50 BYTE) : 게시글 작성자 아이디 (FK)
    private String boardTitle;        //BOARD_TITLE	VARCHAR2(50 BYTE) : 게시글 제목
    private String boardContent;        //BOARD_CONTENT	VARCHAR2(2000 BYTE) : 게시글 내용
    private String boardOriginalFilename;    //BOARD_ORIGINAL_FILENAME	VARCHAR2(100 BYTE) : 첨부파일 원래 이름
    private String boardRenameFilename;    //BOARD_RENAME_FILENAME	VARCHAR2(100 BYTE) : 첨부파일 바뀐 이름
    private int boardReadCount;    //BOARD_READCOUNT	NUMBER : 게시글 조회수
    @JsonFormat(pattern = "yyyy-MM--dd")
    private Date boardDate;    //BOARD_DATE	DATE : 게시글 등록 날짜



    //dto --> entity
    public BoardEntity toEntity() {
       return BoardEntity.builder()
               .boardNum(boardNum)
               .boardWriter(boardWriter)
               .boardTitle(boardTitle)
               .boardContent(boardContent)
               .boardOriginalFilename(boardOriginalFilename)
               .boardRenameFilename(boardRenameFilename)
               .boardReadCount(boardReadCount)
               .boardDate(boardDate)
               .build();
    }
}
