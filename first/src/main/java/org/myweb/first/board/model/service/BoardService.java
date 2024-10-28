package org.myweb.first.board.model.service;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.board.jpa.entity.BoardEntity;
import org.myweb.first.board.jpa.entity.BoardNativeVo;
import org.myweb.first.board.jpa.repository.BoardRepository;
import org.myweb.first.board.model.dto.Board;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
	// 스프링 MVC 구조를 스프링부르에서 그대로 사용해도 됨
	// Service에 대한 interface를 만들고, 해당 서비스를 상속받은 ServiceImpl 클래스를 만드는 구조로 작성해도 됨

	// 인터페이스는 객체 생성하지 않기 때문에 new 객체 생성할 필요 없기 때문에 @Autowired 어노테이션 필요없음
	private final BoardRepository boardRepository;

	public ArrayList<Board> selectTop3() {
		// jpa가 제공하는 메소드를 쓴다면 findAll() 로 모두 조회해 와서 3개만 추출하는 방법 (NoticeService 참조)
		// 필요할 경우 JPA가 제공하는 메소드로는 해결이 안되는 기능을 메소드를 추가해서 사용할 수 있음
		List<BoardNativeVo> nativeList = boardRepository.findTop3();
		//내림차순 정렬이므로 상위 3개만 추출함
		ArrayList<Board> list = new ArrayList<>();
        for (int index = 0; index < 3; index++) {
            Board board = new Board();
			board.setBoardNum(nativeList.get(index).getBoard_num());
			board.setBoardTitle(nativeList.get(index).getBoard_title());
			board.setBoardReadCount(nativeList.get(index).getBoard_readcount());
			list.add(board);
        }
        return list;
	}


	public int selectListCount() {
		// JPA가 제공하는 JpaRepository.count() 메소드를 사용
		return (int) boardRepository.count();
	}


	public ArrayList<Board> selectList(Pageable paging) {
		// JPA 가 제공하는 JpaRepository.findAll(pageable) 메소드를 사용
		Page<BoardEntity> page = boardRepository.findAll(paging);
		ArrayList<Board> list = new ArrayList<>();
		for(BoardEntity entity: page.getContent()){
			list.add(entity.toDto());
		}
		return list;
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
