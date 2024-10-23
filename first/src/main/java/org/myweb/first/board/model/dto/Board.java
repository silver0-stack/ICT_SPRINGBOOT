package org.myweb.first.board.model.dto;

import java.sql.Date;

public class Board implements java.io.Serializable {
	private static final long serialVersionUID = -6086290596677675589L;

	private int boardNum;			//BOARD_NUM	NUMBER : 게시글 번호 (PK)
	private String boardWriter;		//BOARD_WRITER	VARCHAR2(50 BYTE) : 게시글 작성자 아이디 (FK)
	private String boardTitle;		//BOARD_TITLE	VARCHAR2(50 BYTE) : 게시글 제목
	private String boardContent;		//BOARD_CONTENT	VARCHAR2(2000 BYTE) : 게시글 내용
	private String boardOriginalFilename;	//BOARD_ORIGINAL_FILENAME	VARCHAR2(100 BYTE) : 첨부파일 원래 이름
	private String boardRenameFilename;	//BOARD_RENAME_FILENAME	VARCHAR2(100 BYTE) : 첨부파일 바뀐 이름
	private int boardRef;		//BOARD_REF	NUMBER : 참조하는 원글 번호 (원글일 때는 자기번호)
	private int boardReplyRef;	//BOARD_REPLY_REF	NUMBER : 참조하는 댓글번호 
	//(원글 : NULL, 댓글: 자기번호, 대댓글 : 참조하는 댓글번호)
	private int boardLev;		//BOARD_LEV	NUMBER : 원글 : 1, 원글의 댓글 : 2, 댓글의 댓글(대댓글) : 3
	private int boardReplySeq;	//BOARD_REPLY_SEQ	NUMBER : 댓글의 순번 (최근 댓글 | 대댓글을 1로 할 것임)
	private int boardReadCount;	//BOARD_READCOUNT	NUMBER : 게시글 조회수
	private Date boardDate;	//BOARD_DATE	DATE : 게시글 등록 날짜
	
	public Board() {
		super();
	}

	public Board(int boardNum, String boardWriter, String boardTitle, String boardContent, String boardOriginalFilename,
			String boardRenameFilename, int boardRef, int boardReplyRef, int boardLev, int boardReplySeq,
			int boardReadCount, Date boardDate) {
		super();
		this.boardNum = boardNum;
		this.boardWriter = boardWriter;
		this.boardTitle = boardTitle;
		this.boardContent = boardContent;
		this.boardOriginalFilename = boardOriginalFilename;
		this.boardRenameFilename = boardRenameFilename;
		this.boardRef = boardRef;
		this.boardReplyRef = boardReplyRef;
		this.boardLev = boardLev;
		this.boardReplySeq = boardReplySeq;
		this.boardReadCount = boardReadCount;
		this.boardDate = boardDate;
	}

	public int getBoardNum() {
		return boardNum;
	}

	public void setBoardNum(int boardNum) {
		this.boardNum = boardNum;
	}

	public String getBoardWriter() {
		return boardWriter;
	}

	public void setBoardWriter(String boardWriter) {
		this.boardWriter = boardWriter;
	}

	public String getBoardTitle() {
		return boardTitle;
	}

	public void setBoardTitle(String boardTitle) {
		this.boardTitle = boardTitle;
	}

	public String getBoardContent() {
		return boardContent;
	}

	public void setBoardContent(String boardContent) {
		this.boardContent = boardContent;
	}

	public String getBoardOriginalFilename() {
		return boardOriginalFilename;
	}

	public void setBoardOriginalFilename(String boardOriginalFilename) {
		this.boardOriginalFilename = boardOriginalFilename;
	}

	public String getBoardRenameFilename() {
		return boardRenameFilename;
	}

	public void setBoardRenameFilename(String boardRenameFilename) {
		this.boardRenameFilename = boardRenameFilename;
	}

	public int getBoardRef() {
		return boardRef;
	}

	public void setBoardRef(int boardRef) {
		this.boardRef = boardRef;
	}

	public int getBoardReplyRef() {
		return boardReplyRef;
	}

	public void setBoardReplyRef(int boardReplyRef) {
		this.boardReplyRef = boardReplyRef;
	}

	public int getBoardLev() {
		return boardLev;
	}

	public void setBoardLev(int boardLev) {
		this.boardLev = boardLev;
	}

	public int getBoardReplySeq() {
		return boardReplySeq;
	}

	public void setBoardReplySeq(int boardReplySeq) {
		this.boardReplySeq = boardReplySeq;
	}

	public int getBoardReadCount() {
		return boardReadCount;
	}

	public void setBoardReadCount(int boardReadCount) {
		this.boardReadCount = boardReadCount;
	}

	public Date getBoardDate() {
		return boardDate;
	}

	public void setBoardDate(Date boardDate) {
		this.boardDate = boardDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "Board [boardNum=" + boardNum + ", boardWriter=" + boardWriter + ", boardTitle=" + boardTitle
				+ ", boardContent=" + boardContent + ", boardOriginalFilename=" + boardOriginalFilename
				+ ", boardRenameFilename=" + boardRenameFilename + ", boardRef=" + boardRef + ", boardReplyRef="
				+ boardReplyRef + ", boardLev=" + boardLev + ", boardReplySeq=" + boardReplySeq + ", boardReadCount="
				+ boardReadCount + ", boardDate=" + boardDate + "]";
	}


}
