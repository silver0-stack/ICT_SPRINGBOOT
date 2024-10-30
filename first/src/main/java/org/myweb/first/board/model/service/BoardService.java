package org.myweb.first.board.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.data.domain.PageRequest;
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
		// JPA가 제공하는 메소드 사용: findById(id) 로 지정한 프로퍼티의 값(Optinal<Entity>)을 DTO로 변환
		return boardRepository.findById(boardNum).map(BoardEntity::toDto).orElse(null);
	}

	/*
	* 게시글 조회 시 조회수를 1 증가 시키는 메소드
	*
	* @param boardNum 게시글 번호
	* @return 증가된 조회수
	* @throws IllegalArgumentException: boardNum is null 게시글이 존재하지 않을 경우
	* */

	@Transactional
	public int updateAddReadCount(int boardNum) {
		Optional<BoardEntity> optionalBoard = boardRepository.findById(boardNum);

		if(optionalBoard.isPresent()){
			BoardEntity board = optionalBoard.get();
			board.setBoardReadCount(board.getBoardReadCount() + 1);
			// 변경 감지(Dirty Checking)에 의해 트랜잭션 종료 시점에 자동으로 업데이트됨
			return board.getBoardReadCount();
		} else{
			throw new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. 게시글 번호: "+ boardNum );
		}
		
	}


	@Transactional
	public int insertBoard(Board board) {
		// 추가한 메소드 사용: 현재 마지막 게시글 번호 조회용
		board.setBoardNum(boardRepository.findLastBoardNum() + 1); //왜냐면 시퀀스는 0부터 시작이니까
        log.info("BoardService board insert: {}", board);
		// JPA가 제공하는 메소드 사용 : save(Entity)
		// => pk(boardNum)에 해당되는 글 번호가 테이블에 없으면 insert 문 실행함
		// => pk(boardNum)이 테이블에 있으면 update 문 실행함

		try{
			boardRepository.save(board.toEntity());
			return 1;
		}catch(Exception e){
			log.error("BoardService board insert error: {}", e);
			return 0;
		}
	}


	public int updateReplySeq(Board reply) {
		return 0;
	}


	public int insertReply(Board reply) {
		return 0;
	}


	public int deleteBoard(int boardNum) {
		try{
			boardRepository.deleteById(boardNum);
			return 1;
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}


	public int updateReply(Board reply) {
		return 0;
	}


	public int updateOrigin(Board board) {
		try{
			boardRepository.save(board.toEntity());
			return 1;
		}catch(Exception e){
			log.error("BoardService board update error: {}", e);
			return 0;
		}
	}


	public int selectSearchTitleCount(String keyword) {
		// jpa가 제공하는 전체 목록 갯수 조회하는 count() 로는 해결이 안됨
		// 추가 작성해서 사용: 리포티토리 인터페이스에 추가 작성함
		return boardRepository.countSearchTitle(keyword).intValue();
	}


	public int selectSearchWriterCount(String keyword) {
		return boardRepository.countSearchTitle(keyword).intValue();
	}


	public int selectSearchDateCount(Search search) {
		return boardRepository.countSearchDate(search.getBegin(), search.getEnd()).intValue();
	}


	public ArrayList<Board> selectSearchTitle(String keyword, Pageable pageable) {
		return boardRepository.findSearchTitle(keyword, pageable).getContent()
				.stream().map(BoardEntity::toDto).collect(Collectors.toCollection(ArrayList::new));
	}


	public ArrayList<Board> selectSearchWriter(String keyword, Pageable pageable) {
		return boardRepository.findSearchWriter(keyword, pageable).getContent()
				.stream().map(BoardEntity::toDto).collect(Collectors.toCollection(ArrayList::new));
	}


	public ArrayList<Board> selectSearchDate( java.util.Date startDate, java.util.Date endDate, Pageable pageable) {
		return boardRepository.findSearchDate(startDate, endDate, pageable).getContent()
				.stream().map(BoardEntity::toDto).collect(Collectors.toCollection(ArrayList::new));
	}

}
