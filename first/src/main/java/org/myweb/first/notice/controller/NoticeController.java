//package org.myweb.first.notice.controller;
//
//import java.io.File;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.sql.Date;
//import java.util.ArrayList;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import lombok.extern.slf4j.Slf4j;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.myweb.first.common.FileNameChange;
//import org.myweb.first.common.Paging;
//import org.myweb.first.common.Search;
//import org.myweb.first.member.model.dto.Member;
//import org.myweb.first.notice.model.dto.Notice;
//import org.myweb.first.notice.model.service.NoticeService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.ModelAndView;
//
//@Slf4j    //log 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
//@Controller
//public class NoticeController {
//	@Autowired
//	private NoticeService noticeService;
//
//	// 뷰 페이지 이동 처리용 메소드 ---------------------------------------
//	// 새 공지글 등록 페이지로 이동 처리용
//	@RequestMapping("moveWrite.do")
//	public String moveWritePage() {
//		return "notice/noticeWriteForm";
//	}
//
//	// 공지글 수정페이지로 이동 처리용
//	@RequestMapping("nmoveup.do")
//	public ModelAndView moveUpdatePage(@RequestParam("no") int noticeNo, ModelAndView mv) {
//		// 수정페이지에 출력할 공지글 조회해 봄
//		Notice notice = noticeService.selectNotice(noticeNo);
//
//		if (notice != null) {
//			mv.addObject("notice", notice);
//			mv.setViewName("notice/noticeUpdateView");
//		} else {
//			mv.addObject("message", noticeNo + "번 공지글 수정페이지로 이동 실패!");
//			mv.setViewName("common/error");
//		}
//
//		return mv;
//	}
//
//	// 요청 처리용 메소드 -----------------------------------------
//	@RequestMapping(value = "ntop3.do", method = RequestMethod.POST)
//	@ResponseBody
//	public String noticeNewTop3Method(HttpServletResponse response) throws UnsupportedEncodingException {
//		// ajax 요청시 리턴방법은 여러가지가 있음 (문자열, json 객체 등)
//		// json 객체를 response 객체 이용시 2가지중 선택 가능
//		// 방법1 : 출력스트림을 따로 생성해서 응답하는 방법 -> public void 로 지정
//		// 방법2 : 뷰리졸버로 리턴해서 등록된 JSONView 가 내보내는 방법 (servlet-context.xml 에 등록)
//		// public String 으로 지정
//
//		// 최근 등록된 공지글 3개 조회 요청함
//		ArrayList<Notice> list = noticeService.selectTop3();
//
//		// 내보낼 값에 대해 response 에 mimiType 설정
//		response.setContentType("application/json; charset=utf-8");
//
//		// 리턴된 list 를 json 배열에 옮겨 기록하기
//		JSONArray jarr = new JSONArray();
//
//		for (Notice notice : list) {
//			// notice 값들을 저장할 json 객체 생성
//			JSONObject job = new JSONObject(); // org.json.simple.JSONObject 임포트함
//
//			job.put("no", notice.getNoticeNo());
//			// 문자열값에 한글이 포함되어 있다면, 반드시 인코딩해서 저장해야 함
//			// java.net.URLEncoder 의 static 메소드인 encode('한글이있는문자열값', '문자셋') 사용함
//			job.put("title", URLEncoder.encode(notice.getNoticeTitle(), "utf-8"));
//			// 날짜데이터는 반드시 문자열로 바꿔서 저장할 것 : 날짜 그대로 저장하면 뷰에서 json 전체 출력 안 됨
//			job.put("date", notice.getNoticeDate().toString());
//
//			jarr.add(job); // 배열에 추가
//		} // for each
//
//		// 전송용 json 객체 생성함
//		JSONObject sendJson = new JSONObject();
//		// 전송용 json 에 jarr 을 저장함
//		sendJson.put("nlist", jarr);
//
//		return sendJson.toJSONString();
//	}
//
//	// 공지글 상세 내용보기 요청 처리용
//	@RequestMapping("ndetail.do")
//	public ModelAndView noticeDetailMethod(@RequestParam("no") int noticeNo, ModelAndView mv, HttpSession session) {
//		// 관리자용 상세보기 페이지와 일반회원 상세보기 페이지를 구분해서 응답 처리함
//		// 관리자인지 확인하기 위해 session 매개변수 추가함
//
//		log.info("ndetail.do : " + noticeNo); // 전송받은 값 확인
//
//		Notice notice = noticeService.selectNotice(noticeNo);
//
//		// 조회수 1증가 처리
//		noticeService.updateAddReadCount(noticeNo);
//
//		if (notice != null) {
//			mv.addObject("notice", notice);
//
//			Member loginUser = (Member) session.getAttribute("loginUser");
//			if (loginUser != null && loginUser.getAdminYN().equals("Y")) {
//				mv.setViewName("notice/noticeAdminDetailView");
//			} else {
//				mv.setViewName("notice/noticeDetailView");
//			}
//		} else {
//			mv.addObject("message", noticeNo + "번 공지글 상세보기 요청 실패!");
//			mv.setViewName("common/error");
//		}
//
//		return mv;
//	}
//
//	// 공지사항 전체 목록보기 요청 처리용 (페이징 처리 : 한 페이지에 10개씩 출력 처리)
//	@RequestMapping("nlist.do")
//	public ModelAndView noticeListMethod(ModelAndView mv, @RequestParam(name = "page", required = false) String page,
//			@RequestParam(name = "limit", required = false) String slimit) {
//		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수
//
//		// 페이징 처리
//		int currentPage = 1;
//		if (page != null) {
//			currentPage = Integer.parseInt(page);
//		}
//
//		// 한 페이지에 출력할 공지 갯수 10개로 지정
//		int limit = 10;
//		if (slimit != null) {
//			limit = Integer.parseInt(slimit);
//		}
//
//		// 총 목록갯수 조회해서 총 페이지 수 계산함
//		int listCount = noticeService.selectListCount();
//		// 페이지 관련 항목 계산 처리
//		Paging paging = new Paging(listCount, limit, currentPage, "nlist.do");
//		paging.calculate();
//
//		//JPA 가 제공하는 메소드에 필요한 Pageable 객체 생성함 ---------------------------------------
//		Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1, paging.getLimit(),
//				Sort.by(Sort.Direction.DESC, "noticeNo"));
//
//		// 서비스롤 목록 조회 요청하고 결과 받기
//		ArrayList<Notice> list = noticeService.selectList(pageable);
//
//		if (list != null && list.size() > 0) {
//			mv.addObject("list", list);
//			mv.addObject("paging", paging);
//			mv.addObject("currentPage", currentPage);
//
//			mv.setViewName("notice/noticeListView");
//		} else {
//			mv.addObject("message", "목록 조회 실패!");
//			mv.setViewName("common/error");
//		}
//
//		return mv;
//	}
//
//	// 새 공지글 등록 요청 처리용 (파일 업로드 기능 추가)
//	@RequestMapping(value = "ninsert.do", method = RequestMethod.POST)
//	public String noticeInsertMethod(Notice notice, Model model,
//			@RequestParam(name = "ofile", required = false) MultipartFile mfile, HttpServletRequest request) {
//		log.info("ninsert.do : " + notice);
//
//		// 공지사항 첨부파일 저장 폴더를 경로 지정
//		String savePath = request.getSession().getServletContext().getRealPath("resources/notice_upfiles");
//
//		// 첨부파일이 있을 때
//		if (!mfile.isEmpty()) {
//			// 전송온 파일이름 추출함
//			String fileName = mfile.getOriginalFilename();
//			String renameFileName = null;
//
//			// 저장폴더에는 변경된 이름을 저장 처리함
//			// 파일 이름바꾸기함 : 년월일시분초.확장자
//			if (fileName != null && fileName.length() > 0) {
//				// 바꿀 파일명에 대한 문자열 만들기
//				renameFileName = FileNameChange.change(fileName, "yyyyMMddHHmmss");
//				// 바뀐 파일명 확인
//				log.info("첨부파일명 확인 : " + renameFileName);
//
//				try {
//					// 저장 폴더에 파일명 바꾸어 저장하기
//					mfile.transferTo(new File(savePath + "\\" + renameFileName));
//				} catch (Exception e) {
//					e.printStackTrace();
//					model.addAttribute("message", "첨부파일 저장 실패!");
//					return "common/error";
//				}
//			} // 파일명 바꾸기
//
//			// notice 객체에 첨부파일 정보 저장 처리
//			notice.setOriginalFilePath(fileName);
//			notice.setRenameFilePath(renameFileName);
//		} // 첨부파일이 있을 때
//
//		//중요도(importance) 가 null 일때 (체크되지 않았을 때)
//		if(notice.getImportance() == null){
//			notice.setImportance("N");
//		}
//
//		if (noticeService.insertNotice(notice) > 0) {
//			// 새 공지글 등록 성공시 목록 페이지 내보내기 요청
//			return "redirect:nlist.do";
//		} else {
//			model.addAttribute("message", "새 공지글 등록 실패!");
//			return "common/error";
//		}
//	}
//
//	// 첨부파일 다운로드 요청 처리용 메소드
//	// 공통모듈로 작성된 FileDownloadView 클래스를 이용함 => 반드시 리턴타입이 ModelAndView 여야 함
//	@RequestMapping("nfdown.do")
//	public ModelAndView filedownMethod(HttpServletRequest request, ModelAndView mv,
//			@RequestParam("ofile") String originalFileName, @RequestParam("rfile") String renameFileName) {
//
//		// 공지사항 첨부파일 저장 폴더 경로 지정
//		String savePath = request.getSession().getServletContext().getRealPath("resources/notice_upfiles");
//		// 저장 폴더에서 읽을 파일에 대한 File 객체 생성
//		File downFile = new File(savePath + "\\" + renameFileName);
//		// 파일 다운시 브라우저로 내보낼 원래 파일에 대한 File 객체 생성함
//		File originFile = new File(originalFileName);
//
//		// 파일 다운 처리용 뷰클래스 id 명과 다운로드할 File 객체를 ModelAndView 에 담아서 리턴함
//		mv.setViewName("filedown"); // 뷰클래스의 id명 기입
//		mv.addObject("originFile", originFile);
//		mv.addObject("renameFile", downFile);
//
//		return mv;
//	}
//
//	// 공지글 삭제 요청 처리용
//	@RequestMapping("ndelete.do")
//	public String noticeDeleteMethod(@RequestParam("no") int noticeNo,
//			@RequestParam(name = "rfile", required = false) String renameFileName, HttpServletRequest request,
//			Model model) {
//		if (noticeService.deleteNotice(noticeNo) > 0) { // 공지글 삭제 성공시
//			// 공지글 삭제 성공시 저장 폴더에 있는 첨부파일도 삭제 처리함
//			if (renameFileName != null && renameFileName.length() > 0) {
//				// 공지사항 첨부파일 저장 폴더 경로 지정
//				String savePath = request.getSession().getServletContext().getRealPath("resources/notice_upfiles");
//				// 저장 폴더에서 파일 삭제함
//				new File(savePath + "\\" + renameFileName).delete();
//			}
//
//			return "redirect:nlist.do";
//		} else {
//			model.addAttribute("message", noticeNo + "번 공지글 삭제 실패!");
//			return "common/error";
//		}
//	}
//
//	// 공지글 수정 요청 처리용 (파일 업로드 기능 사용)
//	@RequestMapping(value = "nupdate.do", method = RequestMethod.POST)
//	public String noticeUpdateMethod(Notice notice, Model model, HttpServletRequest request,
//			@RequestParam(name = "deleteFlag", required = false) String delFlag,
//			@RequestParam(name = "upfile", required = false) MultipartFile mfile) {
//		log.info("mupdate.do : " + notice); // 전송온 값 확인
//
//		// 중요도 체크 안 한 경우 처리
//		if (notice.getImportance() == null) {
//			notice.setImportance("N");
//			notice.setImpEndDate(new java.sql.Date(System.currentTimeMillis())); // 오늘 날짜를 기본 날짜로 지정함
//		}
//
//		//수정 날짜도 변경
//		notice.setNoticeDate(new Date(System.currentTimeMillis()));
//
//		// 첨부파일 관련 변경 사항 처리
//		// 공지사항 첨부파일 저장 폴더 경로 지정
//		String savePath = request.getSession().getServletContext().getRealPath("resources/notice_upfiles");
//
//		// 1. 원래 첨부파일이 있는데 '파일삭제'를 선택한 경우
//		// 또는 원래 첨부파일이 있는데 새로운 첨부파일로 변경 업로드된 경우
//		// => 이전 파일과 파일정보 삭제함
//		if (notice.getOriginalFilePath() != null && (delFlag != null && delFlag.equals("yes")) || !mfile.isEmpty()) {
//			// 저장 폴더에서 이전 파일은 삭제함
//			new File(savePath + "\\" + notice.getRenameFilePath()).delete();
//			// notice 안의 파일 정보도 삭제함
//			notice.setOriginalFilePath(null);
//			notice.setRenameFilePath(null);
//		}
//
//		// 2. 새로운 첨부파일이 있을 때 또는 변경 첨부파일이 있을 때 (공지글 첨부파일은 1개임)
//		// 즉, upfile 이름으로 전송온 파일이 있다면
//		if (!mfile.isEmpty()) {
//			// 전송온 파일이름 추출함
//			String fileName = mfile.getOriginalFilename();
//			String renameFileName = null;
//
//			// 저장폴더에는 변경된 이름을 저장 처리함
//			// 파일 이름바꾸기함 : 년월일시분초.확장자
//			if (fileName != null && fileName.length() > 0) {
//				// 바꿀 파일명에 대한 문자열 만들기
//				renameFileName = FileNameChange.change(fileName, "yyyyMMddHHmmss");
//				// 바뀐 파일명 확인
//				log.info("첨부파일명 확인 : " + renameFileName);
//
//				try {
//					// 저장 폴더에 파일명 바꾸어 저장하기
//					mfile.transferTo(new File(savePath + "\\" + renameFileName));
//				} catch (Exception e) {
//					e.printStackTrace();
//					model.addAttribute("message", "첨부파일 저장 실패!");
//					return "common/error";
//				}
//			} // 파일명 바꾸기
//
//			// notice 객체에 첨부파일 정보 저장 처리
//			notice.setOriginalFilePath(fileName);
//			notice.setRenameFilePath(renameFileName);
//		} // 첨부파일이 있을 때
//
//		if (noticeService.updateNotice(notice) > 0) { // 공지글 수정 성공시
//			return "redirect:ndetail.do?no=" + notice.getNoticeNo();
//		} else {
//			model.addAttribute("message", notice.getNoticeNo() + "번 공지글 수정 실패!");
//			return "common/error";
//		}
//	}
//
//	// 공지글 제목 검색용 (페이징 처리 포함)
//	@RequestMapping("nsearchTitle.do")
//	public ModelAndView noticeSearchTitleMethod(ModelAndView mv, @RequestParam("action") String action,
//			@RequestParam("keyword") String keyword, @RequestParam(name = "page", required = false) String page,
//			@RequestParam(name = "limit", required = false) String slimit) {
//
//		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수
//
//		// 페이징 처리
//		int currentPage = 1;
//		if (page != null) {
//			currentPage = Integer.parseInt(page);
//		}
//
//		// 한 페이지에 출력할 공지 갯수 10개로 지정
//		int limit = 10;
//		if (slimit != null) {
//			limit = Integer.parseInt(slimit);
//		}
//
//		// 검색결과가 적용된 총 목록갯수 조회해서 총 페이지 수 계산함
//		int listCount = noticeService.selectSearchTitleCount(keyword);
//		// 페이지 관련 항목 계산 처리
//		Paging paging = new Paging(listCount, limit, currentPage, "nsearchTitle.do");
//		paging.calculate();
//
//		//JPA 가 제공하는 메소드에 필요한 Pageable 객체 생성함 ---------------------------------------
//		Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1, paging.getLimit(),
//				Sort.by(Sort.Direction.DESC, "noticeNo"));
//
//		// 서비스롤 목록 조회 요청하고 결과 받기
//		ArrayList<Notice> list = noticeService.selectSearchTitle(keyword, pageable);
//
//		if (list != null && list.size() > 0) {
//			mv.addObject("list", list);
//			mv.addObject("paging", paging);
//			mv.addObject("currentPage", currentPage);
//			mv.addObject("action", action);
//			mv.addObject("keyword", keyword);
//
//			mv.setViewName("notice/noticeListView");
//		} else {
//			mv.addObject("message", action + "에 대한 " + keyword + " 검색 결과가 존재하지 않습니다.");
//			mv.setViewName("common/error");
//		}
//
//		return mv;
//	}
//
//	// 공지글 내용 검색용 (페이징 처리 포함)
//	@RequestMapping("nsearchContent.do")
//	public ModelAndView noticeSearchContentMethod(ModelAndView mv, @RequestParam("action") String action,
//			@RequestParam("keyword") String keyword, @RequestParam(name = "page", required = false) String page,
//			@RequestParam(name = "limit", required = false) String slimit) {
//
//		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수
//
//		// 페이징 처리
//		int currentPage = 1;
//		if (page != null) {
//			currentPage = Integer.parseInt(page);
//		}
//
//		// 한 페이지에 출력할 공지 갯수 10개로 지정
//		int limit = 10;
//		if (slimit != null) {
//			limit = Integer.parseInt(slimit);
//		}
//
//		// 검색결과가 적용된 총 목록갯수 조회해서 총 페이지 수 계산함
//		int listCount = noticeService.selectSearchContentCount(keyword);
//		// 페이지 관련 항목 계산 처리
//		Paging paging = new Paging(listCount, limit, currentPage, "nsearchContent.do");
//		paging.calculate();
//
//		//JPA 가 제공하는 메소드에 필요한 Pageable 객체 생성함 ---------------------------------------
//		Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1, paging.getLimit(),
//				Sort.by(Sort.Direction.DESC, "noticeNo"));
//
//		// 서비스롤 목록 조회 요청하고 결과 받기
//		ArrayList<Notice> list = noticeService.selectSearchContent(keyword, pageable);
//
//		if (list != null && list.size() > 0) {
//			mv.addObject("list", list);
//			mv.addObject("paging", paging);
//			mv.addObject("currentPage", currentPage);
//			mv.addObject("action", action);
//			mv.addObject("keyword", keyword);
//
//			mv.setViewName("notice/noticeListView");
//		} else {
//			mv.addObject("message", action + "에 대한 " + keyword + " 검색 결과가 존재하지 않습니다.");
//			mv.setViewName("common/error");
//		}
//
//		return mv;
//	}
//
//	// 공지글 등록날짜 검색용 (페이징 처리 포함)
//	@RequestMapping("nsearchDate.do")
//	public ModelAndView noticeSearchDateMethod(
//			ModelAndView mv, Search search,
//			@RequestParam("action") String action,
//			@RequestParam(name = "page", required = false) String page,
//			@RequestParam(name = "limit", required = false) String slimit) {
//
//		// page : 출력할 페이지, limit : 한 페이지에 출력할 목록 갯수
//
//		// 페이징 처리
//		int currentPage = 1;
//		if (page != null) {
//			currentPage = Integer.parseInt(page);
//		}
//
//		// 한 페이지에 출력할 공지 갯수 10개로 지정
//		int limit = 10;
//		if (slimit != null) {
//			limit = Integer.parseInt(slimit);
//		}
//
//		// 검색결과가 적용된 총 목록갯수 조회해서 총 페이지 수 계산함
//		int listCount = noticeService.selectSearchDateCount(search);
//		// 페이지 관련 항목 계산 처리
//		Paging paging = new Paging(listCount, limit, currentPage, "nsearchDate.do");
//		paging.calculate();
//
//		//JPA 가 제공하는 메소드에 필요한 Pageable 객체 생성함 ---------------------------------------
//		Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1, paging.getLimit(),
//				Sort.by(Sort.Direction.DESC, "noticeNo"));
//
//		// 서비스롤 목록 조회 요청하고 결과 받기
//		ArrayList<Notice> list = noticeService.selectSearchDate(search, pageable);
//
//		if (list != null && list.size() > 0) {
//			mv.addObject("list", list);
//			mv.addObject("paging", paging);
//			mv.addObject("currentPage", currentPage);
//			mv.addObject("action", action);
//			mv.addObject("begin", search.getBegin());
//			mv.addObject("end", search.getEnd());
//
//			mv.setViewName("notice/noticeListView");
//		} else {
//			mv.addObject("message", action + "에 대한 " + search.getBegin() + "부터 "
//						+ search.getEnd() + "기간 사이에 등록한 공지글 검색 결과가 존재하지 않습니다.");
//			mv.setViewName("common/error");
//		}
//
//		return mv;
//	}
//}
