package org.myweb.first.notice.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.myweb.first.common.ApiResponse;
import org.myweb.first.common.FileNameChange;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.myweb.first.member.model.dto.Member;
import org.myweb.first.notice.jpa.entity.NoticeEntity;
import org.myweb.first.notice.model.dto.Notice;
import org.myweb.first.notice.model.service.NoticeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Slf4j    //log 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@RestController
@RequestMapping("/api/notices")
public class NoticeController {
	@Autowired
	private NoticeService noticeService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Notice>>> getNoticeList(Pageable pageable){
        try{
            Page<Notice> noticePage = noticeService.getNoticeWithPagination(pageable);
            ApiResponse<Page<Notice>> response = ApiResponse.<Page<Notice>>builder()
                    .success(true)
                    .message("공지 목록 조회 성공")
                    .data(noticePage)
                    .build();
            return ResponseEntity.ok(response);
        }catch(Exception e){
            log.error("공지사항 목록 조회 실패: {}", e.getMessage());
            ApiResponse<Page<Notice>> response = ApiResponse.<Page<Notice>>builder()
                    .success(false)
                    .message("공지사항 목록 조회 실패")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    /**
     * 공지사항 상세 조회(조회수 증가 포함)
     * @param notId
     * @return
     */
    @GetMapping("/{notId}")
    public ResponseEntity<ApiResponse<Notice>> getNoticeById(@PathVariable String notId){
        try{
            Notice noticeEntity = noticeService.getNoticeById(notId);
            ApiResponse<Notice> response = ApiResponse.<Notice>builder()
                    .success(true)
                    .message("공지 상세조회 성공")
                    .data(noticeEntity)
                    .build();
            return ResponseEntity.ok(response);
        }catch(IllegalArgumentException e){
            log.warn("공지 상세조회 실패: {}", e.getMessage());
            ApiResponse<Notice> response = ApiResponse.<Notice>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }catch (Exception e){
            log.error("공지 상세조회 실패: {}", e.getMessage());
            ApiResponse<Notice> response = ApiResponse.<Notice>builder()
                    .success(false)
                    .message("공지 상세조회 중 오류 발생")
                    .build();
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 공지사항 수정
     * @param notId
     * @param updatedNotice
     * @return
     */
    @PutMapping("/{notId}")
    public ResponseEntity<ApiResponse<Notice>> updateNotice(@PathVariable String notId, @RequestBody Notice updatedNotice){
        try {
            Notice notice = noticeService.updateNotice(notId, updatedNotice);
            ApiResponse<Notice> response = ApiResponse.<Notice>builder()
                    .success(true)
                    .message("공지 수정 성공")
                    .data(notice)
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("공지 수정 실패: {}", e.getMessage());
            ApiResponse<Notice> response = ApiResponse.<Notice>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("공지 수정 실패: {}", e.getMessage());
            ApiResponse<Notice> response = ApiResponse.<Notice>builder()
                    .success(false)
                    .message("공지 수정 중 오류 발생")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    /**
     * 공지사항 삭제
     * @param notId
     * @return
     */
    @DeleteMapping("/{notId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(@PathVariable String notId){
        try {
            noticeService.deleteNotice(notId);
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(true)
                    .message("공지 삭제 성공")
                    .build();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("공지 삭제 실패: {}", e.getMessage());
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("공지 삭제 실패: {}", e.getMessage());
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .message("공지 삭제 중 오류 발생")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    /**
     * 최근 공지사항 3개 조회
     * @return
     */
    @GetMapping("/top3")
    public ResponseEntity<ApiResponse<List<Notice>>> getTop3Notice(){
        try {
            List<Notice> topNotices = noticeService.getTop3Notices();
            ApiResponse<List<Notice>> response = ApiResponse.<List<Notice>>builder()
                    .success(true)
                    .message("상위 3개 공지 조회 성공")
                    .data(topNotices)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("상위 3개 공지 조회 실패: {}", e.getMessage());
            ApiResponse<List<Notice>> response = ApiResponse.<List<Notice>>builder()
                    .success(false)
                    .message("상위 3개 공지 조회 중 오류 발생")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
