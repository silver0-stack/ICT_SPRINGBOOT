package org.myweb.first.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.myweb.first.board.model.dto.Board;
import org.myweb.first.board.model.service.BoardService;
import org.myweb.first.board.model.service.ReplyService;
import org.myweb.first.common.FileNameChange;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

@Slf4j
@Controller
public class BoardController {

	@Autowired
	private BoardService boardService;

	@Autowired
	private ReplyService replyService;

	// 뷰 페이지 이동 처리용 메소드 ---------------------------------------
	// 새 게시 원글 등록 페이지로 이동 처리용
	@RequestMapping("bwform.do")
	public String moveWritePage() {
		return "board/boardWriteForm";
	}

	// 게시글(원글) 수정페이지로 이동 처리용
	@RequestMapping("bupview.do")
	public String moveBoardUpdatePage(
			@RequestParam("bnum") int boardNum,
			@RequestParam("page") int currentPage,
			Model model) {
		// 수정페이지에 전달해서 출력할 board 정보 조회함
		Board board = boardService.selectBoard(boardNum);

		if (board != null) {
			model.addAttribute("board", board);
			model.addAttribute("currentPage", currentPage);

			return "board/boardUpdateView";
		} else {
			model.addAttribute("message", boardNum + "번 게시글 수정페이지로 이동 실패!");
			return "common/error";
		}
	}

	// 요청 처리용 메소드 ---------------------------------------------------

	// ajax 요청 : 조회수 많은 인기 게시글 top-3 요청 처리용
	@RequestMapping(value = "btop3.do", method = RequestMethod.POST)
	@ResponseBody
	public String boardTop3Method(HttpServletResponse response) throws UnsupportedEncodingException {
		// ajax 요청시 리턴방법은 여러가지가 있음 (문자열, json 객체 등)
		// json 객체를 response 객체 이용시 2가지중 선택 가능
		// 방법1 : 출력스트림을 따로 생성해서 응답하는 방법 -> public void 로 지정
		// 방법2 : 뷰리졸버로 리턴해서 등록된 JSONView 가 내보내는 방법 (servlet-context.xml 에 등록)
		// public String 으로 지정

		// 조회수 많은 게시글 3개 조회 요청함
		ArrayList<Board> list = boardService.selectTop3();

		// 내보낼 값에 대해 response 에 mimiType 설정
		response.setContentType("application/json; charset=utf-8");

		// 리턴된 list 를 json 배열에 옮겨 기록하기
		JSONArray jarr = new JSONArray();

		for (Board board : list) {
			// notice 값들을 저장할 json 객체 생성
			JSONObject job = new JSONObject(); // org.json.simple.JSONObject 임포트함

			job.put("bnum", board.getBoardNum());
			// 문자열값에 한글이 포함되어 있다면, 반드시 인코딩해서 저장해야 함
			// java.net.URLEncoder 의 static 메소드인 encode('한글이있는문자열값', '문자셋') 사용함
			job.put("btitle", URLEncoder.encode(board.getBoardTitle(), "utf-8"));
			// 조회수
			job.put("rcount", board.getBoardReadCount());

			jarr.add(job); // 배열에 추가
		} // for each

		// 전송용 json 객체 생성함
		JSONObject sendJson = new JSONObject();
		// 전송용 json 에 jarr 을 저장함
		sendJson.put("blist", jarr);

		return sendJson.toJSONString();
	} // btop3.do

	// 게시글 전체 목록보기 요청 처리용 (페이징 처리 : 한 페이지에 10개씩 출력 처리)
	@RequestMapping("blist.do")
	public ModelAndView boardListMethod(
			ModelAndView mv,
			@RequestParam(name = "page", required = false) String page,
			@RequestParam(name = "limit", required = false) String slimit) {
		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수

		// 페이징 처리
		int currentPage = 1;
		if (page != null) {
			currentPage = Integer.parseInt(page);
		}

		// 한 페이지에 출력할 공지 갯수 10개로 지정
		int limit = 10;
		if (slimit != null) {
			limit = Integer.parseInt(slimit);
		}

		// 총 목록갯수 조회해서 총 페이지 수 계산함
		int listCount = boardService.selectListCount();
		// 페이지 관련 항목 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "blist.do");
		paging.calculate();

		// JPA 에 사용될 Pageable 객체 생성
		Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1, paging.getLimit(), Sort.by(Sort.Direction.DESC, "boardNum"));

		// 서비스롤 목록 조회 요청하고 결과 받기
		/*
		 * board_num | board_lev | board_ref | board_reply_ref | board_reply_seq 원글 10번
		 * 10 1 10 null 1 (defaule) 원글 10변의 댓글 23번 (댓글순번 1) 23 2 10 23 1 댓글 23번의 대댓글 27
		 * (대댓글순번 1) 27 3 10 23 1 댓글 23번의 대댓글 24 (대댓글순번 2) 24 3 10 23 2 원글 10번의 댓글 15번
		 * (댓글순번 2) 15 2 10 15 2 댓글 15번의 대댓글 20 (대댓글순번 1) 20 3 10 15 1 댓글 15번의 대댓글 17
		 * (대댓글순번 2) 17 3 10 15 2
		 *
		 * 형태로 목록 조회할 것임
		 */
		ArrayList<Board> list = boardService.selectList(pageable);

		if (list != null && list.size() > 0) {
			mv.addObject("list", list);
			mv.addObject("paging", paging);
			mv.addObject("currentPage", currentPage);

			mv.setViewName("board/boardListView");
		} else {
			mv.addObject("message", currentPage + " 페이지 목록 조회 실패!");
			mv.setViewName("common/error");
		}

		return mv;
	} // blist.do

	// 게시글(원글, 댓글, 대댓글) 상세 내용보기 요청 처리용
	@RequestMapping("bdetail.do")
	public ModelAndView boardDetailMethod(@RequestParam("bnum") int boardNum, @RequestParam("page") int currentPage,
										  ModelAndView mv) {
		log.info("ndetail.do : " + boardNum); // 전송받은 값 확인

		Board board = boardService.selectBoard(boardNum);

		// 조회수 1증가 처리
		boardService.updateAddReadCount(boardNum);

		if (board != null) {
			mv.addObject("board", board);
			mv.addObject("currentPage", currentPage);
			mv.setViewName("board/boardDetailView");

		} else {
			mv.addObject("message", boardNum + "번 게시글 상세보기 요청 실패!");
			mv.setViewName("common/error");
		}

		return mv;
	} // bdetail.do

	// 첨부파일 다운로드 요청 처리용 메소드
	// 공통모듈로 작성된 FileDownloadView 클래스를 이용함 => 반드시 리턴타입이 ModelAndView 여야 함
	@RequestMapping("bfdown.do")
	public ModelAndView filedownMethod(HttpServletRequest request, ModelAndView mv,
									   @RequestParam("ofile") String originalFileName, @RequestParam("rfile") String renameFileName) {

		// 게시글 첨부파일 저장 폴더 경로 지정
		String savePath = request.getSession().getServletContext().getRealPath("resources/board_upfiles");
		// 저장 폴더에서 읽을 파일에 대한 File 객체 생성
		File downFile = new File(savePath + "\\" + renameFileName);
		// 파일 다운시 브라우저로 내보낼 원래 파일에 대한 File 객체 생성함
		File originFile = new File(originalFileName);

		// 파일 다운 처리용 뷰클래스 id 명과 다운로드할 File 객체를 ModelAndView 에 담아서 리턴함
		mv.setViewName("filedown"); // 뷰클래스의 id명 기입
		mv.addObject("originFile", originFile);
		mv.addObject("renameFile", downFile);

		return mv;
	} // bfdown.do

	// 새 게시 원글 등록 요청 처리용 (파일 업로드 기능 추가)
	@RequestMapping(value = "binsert.do", method = RequestMethod.POST)
	public String boardInsertMethod(Board board, Model model,
									@RequestParam(name = "ofile", required = false) MultipartFile mfile, HttpServletRequest request) {
		log.info("binsert.do : " + board); // 전송온 값 저장 확인

		// 게시 원글 첨부파일 저장 폴더를 경로 지정
		String savePath = request.getSession().getServletContext().getRealPath("resources/board_upfiles");

		// 첨부파일이 있을 때
		if (!mfile.isEmpty()) {
			// 전송온 파일이름 추출함
			String fileName = mfile.getOriginalFilename();
			String renameFileName = null;

			// 저장폴더에는 변경된 이름을 저장 처리함
			// 파일 이름바꾸기함 : 년월일시분초.확장자
			if (fileName != null && fileName.length() > 0) {
				// 바꿀 파일명에 대한 문자열 만들기
				renameFileName = FileNameChange.change(fileName, "yyyyMMddHHmmss");
				// 바뀐 파일명 확인
				log.info("첨부파일명 확인 : " + renameFileName);

				try {
					// 저장 폴더에 파일명 바꾸어 저장하기
					mfile.transferTo(new File(savePath + "\\" + renameFileName));
				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute("message", "첨부파일 저장 실패!");
					return "common/error";
				}
			} // 파일명 바꾸기

			// board 객체에 첨부파일 정보 저장 처리
			board.setBoardOriginalFilename(fileName);
			board.setBoardRenameFilename(renameFileName);
		} // 첨부파일이 있을 때

		if (boardService.insertBoard(board) > 0) {
			// 새 게시 원글 등록 성공시 목록 페이지 내보내기 요청
			return "redirect:blist.do";
		} else {
			model.addAttribute("message", "새 게시 원글 등록 실패!");
			return "common/error";
		}
	} // binsert.do

	// 게시 댓글, 대댓글 등록 요청 처리용 (파일 업로드 기능 없음)
	@RequestMapping(value = "breply.do", method = RequestMethod.POST)
	public String replyInsertMethod(Board reply, Model model, @RequestParam("bnum") int bnum,
									@RequestParam("page") int page) {
		// 1. 새로 등록할 댓글은 원글을 조회해 옴 또는 등록할 대댓글은 참조하는 댓글을 조회해 옴
		Board origin = boardService.selectBoard(bnum);

		// 2. 새로 등록할 댓글 또는 대댓글의 레벨을 지정함
//      reply.setBoardLev(origin.getBoardLev() + 1);

		// 3. 참조 원글 번호(boardRef) 지정함
//      reply.setBoardRef(origin.getBoardRef());

		// 4. 새로 등록할 reply 이 대댓글(boardLev : 3)이면, 참조 댓글번호(boardReplyRef) 지정함
//      if (reply.getBoardLev() == 3) {
		// 참조댓글번호 지정함
//         reply.setBoardReplyRef(origin.getBoardReplyRef());
//      }

//      // 5. 최근 등록 댓글 | 대댓글의 순번을 1로 지정함
//      reply.setBoardReplySeq(1);
		// 6. 기존 같은 레벨 & 같은 원글|댓글에 기록된 글은 순번은 1증가시킴
		boardService.updateReplySeq(reply);

		if (boardService.insertReply(reply) > 0) {
			return "redirect:blist.do?page=" + page;
		} else {
			model.addAttribute("message", bnum + "번 글에 대한 댓글 | 대댓글 등록 실패!");
			return "common/error";
		}
	} // breply.do

	// 게시글 (원글, 댓글, 대댓글) 삭제 요청 처리용
	@RequestMapping("bdelete.do")
	public String boardDeleteMethod(Board board, Model model, HttpServletRequest request) {

		if (boardService.deleteBoard(board) > 0) { // 게시글 삭제 성공시
			// 게시글 삭제 성공시 저장 폴더에 있는 첨부파일도 삭제 처리함
			if (board.getBoardRenameFilename() != null && board.getBoardRenameFilename().length() > 0) {
				// 게시글 첨부파일 저장 폴더 경로 지정
				String savePath = request.getSession().getServletContext().getRealPath("resources/board_upfiles");
				// 저장 폴더에서 파일 삭제함
				new File(savePath + "\\" + board.getBoardRenameFilename()).delete();
			}

			return "redirect:blist.do";
		} else {
			model.addAttribute("message", board.getBoardNum() + "번 게시글 삭제 실패!");
			return "common/error";
		}
	}

	// 댓글, 대댓글 수정 요청 처리용
	@RequestMapping(value = "breplyupdate.do", method = RequestMethod.POST)
	public String replyUpdateMethod(Board reply, @RequestParam("page") int currentPage, Model model) {

		if (boardService.updateReply(reply) > 0) {
			// 댓글, 대댓글 수정 성공시 다시 상세보기가 수정된 내용을 보여지게 처리
			model.addAttribute("bnum", reply.getBoardNum());
			model.addAttribute("page", currentPage);

			return "redirect:bdetail.do";
		} else {
			model.addAttribute("message", reply.getBoardNum() + "번 글 수정 실패!");
			return "common/error";
		}
	}

	// 게시 원글 수정 요청 처리용 (파일 업로드 기능 사용)
	@RequestMapping(value = "borginupdate.do", method = RequestMethod.POST)
	public String originUpdateMethod(Board board, Model model, HttpServletRequest request,
									 @RequestParam(name = "page", required = false) String page,
									 @RequestParam(name = "deleteFlag", required = false) String delFlag,
									 @RequestParam(name = "upfile", required = false) MultipartFile mfile) {
		log.info("borginupdate.do : " + board); // 전송온 값 확인

		int currentPage = 1;
		if (page != null) {
			currentPage = Integer.parseInt(page);
		}

		// 첨부파일 관련 변경 사항 처리
		// 게시원글 첨부파일 저장 폴더 경로 지정
		String savePath = request.getSession().getServletContext().getRealPath("resources/board_upfiles");

		// 1. 원래 첨부파일이 있는데 '파일삭제'를 선택한 경우
		// 또는 원래 첨부파일이 있는데 새로운 첨부파일로 변경 업로드된 경우
		// => 이전 파일과 파일정보 삭제함
		if (board.getBoardOriginalFilename() != null && (delFlag != null && delFlag.equals("yes"))) {
			// 저장 폴더에서 이전 파일은 삭제함
			new File(savePath + "\\" + board.getBoardRenameFilename()).delete();
			// board 안의 파일 정보도 삭제함
			board.setBoardOriginalFilename(null);
			board.setBoardRenameFilename(null);
		}

		// 2. 새로운 첨부파일이 있을 때 또는 변경 첨부파일이 있을 때 (공지글 첨부파일은 1개임)
		// 즉, upfile 이름으로 전송온 파일이 있다면
		if (!mfile.isEmpty()) {
			// 전송온 파일이름 추출함
			String fileName = mfile.getOriginalFilename();
			String renameFileName = null;

			// 저장폴더에는 변경된 이름을 저장 처리함
			// 파일 이름바꾸기함 : 년월일시분초.확장자
			if (fileName != null && fileName.length() > 0) {
				// 바꿀 파일명에 대한 문자열 만들기
				renameFileName = FileNameChange.change(fileName, "yyyyMMddHHmmss");
				// 바뀐 파일명 확인
				log.info("첨부파일명 확인 : " + renameFileName);

				try {
					// 저장 폴더에 파일명 바꾸어 저장하기
					mfile.transferTo(new File(savePath + "\\" + renameFileName));
				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute("message", "첨부파일 저장 실패!");
					return "common/error";
				}
			} // 파일명 바꾸기

			// board 객체에 변경된 첨부파일 정보 저장 처리
			board.setBoardOriginalFilename(fileName);
			board.setBoardRenameFilename(renameFileName);
		} // 첨부파일이 있을 때

		if (boardService.updateOrigin(board) > 0) { // 게시원글 수정 성공시
			model.addAttribute("bnum", board.getBoardNum());
			model.addAttribute("page", currentPage);

			return "redirect:bdetail.do";
		} else {
			model.addAttribute("message", board.getBoardNum() + "번 게시 원글 수정 실패!");
			return "common/error";
		}
	}

	// *****************************************************************
	// 공지글 제목 검색용 (페이징 처리 포함)
	@RequestMapping("bsearchTitle.do")
	public ModelAndView boardSearchTitleMethod(
			ModelAndView mv,
			@RequestParam("action") String action,
			@RequestParam("keyword") String keyword,
			@RequestParam(name = "page", required = false) String page,
			@RequestParam(name = "limit", required = false) String slimit) {

		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수

		// 페이징 처리
		int currentPage = 1;
		if (page != null) {
			currentPage = Integer.parseInt(page);
		}

		// 한 페이지에 출력할 공지 갯수 10개로 지정
		int limit = 10;
		if (slimit != null) {
			limit = Integer.parseInt(slimit);
		}

		// 검색결과가 적용된 총 목록갯수 조회해서 총 페이지 수 계산함
		int listCount = boardService.selectSearchTitleCount(keyword);
		// 페이지 관련 항목 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "bsearchTitle.do");
		paging.calculate();

		// 마이바티스 매퍼에서 사용되는 메소드는 Object 1개만 전달할 수 있음
		// paging.startRow, paging.endRow + keyword 같이 전달해야 하므로 => 하나의 객체로 만들어야 함
		Search search = new Search();
		search.setKeyword(keyword);
		search.setStartRow(paging.getStartRow());
		search.setEndRow(paging.getEndRow());

		// 서비스롤 목록 조회 요청하고 결과 받기
		ArrayList<Board> list = boardService.selectSearchTitle(search);

		if (list != null && list.size() > 0) {
			mv.addObject("list", list);
			mv.addObject("paging", paging);
			mv.addObject("currentPage", currentPage);
			mv.addObject("action", action);
			mv.addObject("keyword", keyword);

			mv.setViewName("board/boardListView");
		} else {
			mv.addObject("message", action + "에 대한 " + keyword + " 검색 결과가 존재하지 않습니다.");
			mv.setViewName("common/error");
		}

		return mv;
	}

	// 게시글 작성자 검색용 (페이징 처리 포함)
	@RequestMapping("bsearchWriter.do")
	public ModelAndView boardSearchWriterMethod(
			ModelAndView mv,
			@RequestParam("action") String action,
			@RequestParam("keyword") String keyword,
			@RequestParam(name = "page", required = false) String page,
			@RequestParam(name = "limit", required = false) String slimit) {

		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수

		// 페이징 처리
		int currentPage = 1;
		if (page != null) {
			currentPage = Integer.parseInt(page);
		}

		// 한 페이지에 출력할 공지 갯수 10개로 지정
		int limit = 10;
		if (slimit != null) {
			limit = Integer.parseInt(slimit);
		}

		// 검색결과가 적용된 총 목록갯수 조회해서 총 페이지 수 계산함
		int listCount = boardService.selectSearchWriterCount(keyword);
		// 페이지 관련 항목 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "bsearchWriter.do");
		paging.calculate();

		// 마이바티스 매퍼에서 사용되는 메소드는 Object 1개만 전달할 수 있음
		// paging.startRow, paging.endRow + keyword 같이 전달해야 하므로 => 하나의 객체로 만들어야 함
		Search search = new Search();
		search.setKeyword(keyword);
		search.setStartRow(paging.getStartRow());
		search.setEndRow(paging.getEndRow());

		// 서비스롤 목록 조회 요청하고 결과 받기
		ArrayList<Board> list = boardService.selectSearchWriter(search);

		if (list != null && list.size() > 0) {
			mv.addObject("list", list);
			mv.addObject("paging", paging);
			mv.addObject("currentPage", currentPage);
			mv.addObject("action", action);
			mv.addObject("keyword", keyword);

			mv.setViewName("board/boardListView");
		} else {
			mv.addObject("message", action + "에 대한 " + keyword + " 검색 결과가 존재하지 않습니다.");
			mv.setViewName("common/error");
		}

		return mv;
	}

	// 게시 원글 등록날짜 검색용 (페이징 처리 포함)
	@RequestMapping("bsearchDate.do")
	public ModelAndView boardSearchDateMethod(
			ModelAndView mv, Search search,
			@RequestParam("action") String action,
			@RequestParam(name = "page", required = false) String page,
			@RequestParam(name = "limit", required = false) String slimit) {

		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수

		// 페이징 처리
		int currentPage = 1;
		if (page != null) {
			currentPage = Integer.parseInt(page);
		}

		// 한 페이지에 출력할 공지 갯수 10개로 지정
		int limit = 10;
		if (slimit != null) {
			limit = Integer.parseInt(slimit);
		}

		// 검색결과가 적용된 총 목록갯수 조회해서 총 페이지 수 계산함
		int listCount = boardService.selectSearchDateCount(search);
		// 페이지 관련 항목 계산 처리
		Paging paging = new Paging(listCount, limit, currentPage, "bsearchDate.do");
		paging.calculate();

		// 마이바티스 매퍼에서 사용되는 메소드는 Object 1개만 전달할 수 있음
		// paging.startRow, paging.endRow + search.begin, search.end 같이 전달해야 하므로 => 하나의
		// 객체로 만들어야 함
		search.setStartRow(paging.getStartRow());
		search.setEndRow(paging.getEndRow());

		// 서비스롤 목록 조회 요청하고 결과 받기
		ArrayList<Board> list = boardService.selectSearchDate(search);

		if (list != null && list.size() > 0) {
			mv.addObject("list", list);
			mv.addObject("paging", paging);
			mv.addObject("currentPage", currentPage);
			mv.addObject("action", action);
			mv.addObject("begin", search.getBegin());
			mv.addObject("end", search.getEnd());

			mv.setViewName("board/boardListView");
		} else {
			mv.addObject("message", action + "에 대한 " + search.getBegin() + "부터 " + search.getEnd()
					+ "기간 사이에 등록한 공지글 검색 결과가 존재하지 않습니다.");
			mv.setViewName("common/error");
		}

		return mv;
	}
}
