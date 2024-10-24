package org.myweb.first.board.model.service;

import org.myweb.first.board.model.dto.Board;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service("boardService")
public class BoardService {
//
//	@Autowired
//	private BoardDao boardDao;

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
