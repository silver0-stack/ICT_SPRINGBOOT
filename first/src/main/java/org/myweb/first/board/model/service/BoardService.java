package org.myweb.first.board.model.service;

import java.util.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.board.model.dto.Board;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {




	public ArrayList<Board> selectTop3() {
		return null;
	}


	public int selectListCount() {
		return 0;
	}


	public ArrayList<Board> selectList(Paging paging) {
		return null;
	}


	public Board selectBoard(int boardNum) {
		return null;
	}


	public int updateAddReadCount(int boardNum) {
		return 0;
		
	}


	public int insertBoard(Board board) {
		return 0;
	}


	public int updateReplySeq(Board reply) {
		return 0;
	}


	public int insertReply(Board reply) {
		return 0;
	}


	public int deleteBoard(Board board) {
		return 0;
	}


	public int updateReply(Board reply) {
		return 0;
	}


	public int updateOrigin(Board board) {
		return 0;
	}


	public int selectSearchTitleCount(String keyword) {
		return 0;
	}


	public int selectSearchWriterCount(String keyword) {
		return 0;
	}


	public int selectSearchDateCount(Search search) {
		return 0;
	}


	public ArrayList<Board> selectSearchTitle(Search search) {
		return null;
	}


	public ArrayList<Board> selectSearchWriter(Search search) {
		return null;
	}


	public ArrayList<Board> selectSearchDate(Search search) {
		return null;
	}
}
