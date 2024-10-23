package org.myweb.first.board.model.service;

import java.util.ArrayList;

import org.myweb.first.board.model.dto.Board;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.myweb.first.notice.model.dto.Notice;

public interface BoardService {
	ArrayList<Board> selectTop3();
	int selectListCount();  //게시글 전체 갯수 조회용
	ArrayList<Board> selectList(Paging paging);  //한 페이지에 출력할 목록 조회용(원글, 댓글, 대댓글 포함)
	Board selectBoard(int boardNum);  //게시글 상세보기 조회용
	int updateAddReadCount(int boardNum);  //조회수 1증가 처리용
	//dml 관련
	int insertBoard(Board board);  //게시 원글 등록 처리용
	int updateReplySeq(Board reply);  //기존 등록 댓글 | 대댓글 순번 1증가 처리용
	int insertReply(Board reply);  //게시 댓글 | 대댓글 등록 처리용
	int deleteBoard(Board board);  //게시글 (원글, 댓글, 대댓글) 삭제 처리용
	int updateReply(Board reply);	//댓글, 대댓글 수정 처리용
	int updateOrigin(Board board);	//원글 수정 처리용
	//검색 관련
	int selectSearchTitleCount(String keyword);
	int selectSearchWriterCount(String keyword);
	int selectSearchDateCount(Search search);
	ArrayList<Board> selectSearchTitle(Search search);
	ArrayList<Board> selectSearchWriter(Search search);
	ArrayList<Board> selectSearchDate(Search search);
}
