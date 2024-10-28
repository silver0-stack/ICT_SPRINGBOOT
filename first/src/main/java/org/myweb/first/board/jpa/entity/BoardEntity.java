package org.myweb.first.board.jpa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


// 테이블 생성에 대한 가이드 클래스임
// @Entity 어노테이션 표시함 -> 엔터티로 자동 등록됨 => JpaRepository에서 CRUD를 자동으로 처리함
@Data //@Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor, @Getter, @Setter, @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "BOARD")
@Entity // JAP가 관리함. 테이블의 컬럼과 DTO 클래스의 프로퍼티를 매핑하는 역할을 함
public class BoardEntity {
    @Id // JPA에서 @Id로 선언한 PK 속성은 id로 인식됨
    @Column(name = "BOARD_NUM", nullable = false)
    private int boardNum;            //BOARD_NUM	NUMBER : 게시글 번호 (PK)
    @Column(name = "BOARD_WRITER", nullable = false, length = 50, unique = true)
    private String boardWriter;        //BOARD_WRITER	VARCHAR2(50 BYTE) : 게시글 작성자 아이디 (FK)
    @Column(name = "BOARD_TITLE", nullable = false, length = 50)
    private String boardTitle;        //BOARD_TITLE	VARCHAR2(50 BYTE) : 게시글 제목
    @Column(name = "BOARD_CONTENT", nullable = false, length = 2000)  // 2000 byte = 2KB, 2MB = 2048KB, 2GB = 2097152KB, 2TB = 2147483648KB  // 2MB 이상의 파일을 upload할 수 없음
    private String boardContent;        //BOARD_CONTENT	VARCHAR2(2000 BYTE) : 게시글 내용
    @Column(name="BOARD_ORIGINAL_FILENAME", nullable = false, length = 100, unique = true)  // 100 byte = 1KB, 1MB = 10
    private String boardOriginalFilename;    //BOARD_ORIGINAL_FILENAME	VARCHAR2(100 BYTE) : 첨부파일 원래 이름
    @Column(name="BOARD_RENAME_FILENAME", nullable = false, length = 100, unique = true)  // 100 byte = 1KB, 1
    private String boardRenameFilename;    //BOARD_RENAME_FILENAME	VARCHAR2(100 BYTE) : 첨부파일 바뀐 이름
    @Column(name="BOARD_READCOUNT", nullable = false, columnDefinition = "0  DEFAULT 0  NOT NULL  CHECK (BOARD_READCOUNT >= 0)  -- 0 이상의 값만 ��")
    private int boardReadCount;    //BOARD_READCOUNT	NUMBER : 게시글 조회수
    @Column(name="BOARD_DATE", nullable = false)  // 2000 byte = 2KB, 2MB = 2048KB, 2GB = 2097152KB, 2TB = 2147483648KB  // 2MB 이상의 파일을 upload할 수 없음
    @JsonFormat(pattern = "yyyy-MM--dd")
    private Date boardDate;    //BOARD_DATE	DATE : 게시글 등록 날짜
}
