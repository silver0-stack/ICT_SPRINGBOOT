package org.myweb.first.files.notice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.common.ApiResponse;
import org.myweb.first.files.notice.model.dto.NoticeFiles;
import org.myweb.first.files.notice.model.service.NoticeFilesService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notice-files")
@RequiredArgsConstructor
public class NoticeFilesController {

    private final NoticeFilesService noticeFilesService;

    /**
     * 공지사항 파일 업로드
     *
     * @param noticeId 공지사항 ID
     * @param file     업로드할 파일
     * @return ApiResponse<NoticeFiles>
     */
    @PostMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeFiles>> uploadNoticeFile(
            @PathVariable String noticeId,
            @RequestParam("file") MultipartFile file) {
        try {
            NoticeFiles uploadedFile = noticeFilesService.uploadNoticeFile(noticeId, file);
            ApiResponse<NoticeFiles> response = ApiResponse.<NoticeFiles>builder()
                    .success(true)
                    .message("파일 업로드 성공")
                    .data(uploadedFile)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            ApiResponse<NoticeFiles> response = ApiResponse.<NoticeFiles>builder()
                    .success(false)
                    .message("파일 업로드 실패")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 공지사항 파일 삭제
     *
     * @param fileId 파일 ID
     * @return ApiResponse<Void>
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse<Void>> deleteNoticeFile(@PathVariable String fileId) {
        try {
            noticeFilesService.deleteNoticeFile(fileId);
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(true)
                    .message("파일 삭제 성공")
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .message("파일 삭제 중 오류가 발생했습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 공지사항 파일 목록 조회
     *
     * @param noticeId 공지사항 ID
     * @return ApiResponse<List < NoticeFiles>>
     */
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<List<NoticeFiles>>> getNoticeFiles(@PathVariable String noticeId) {
        try {
            List<NoticeFiles> files = noticeFilesService.getNoticeFiles(noticeId);

            if (files.isEmpty()) {
                ApiResponse<List<NoticeFiles>> response = ApiResponse.<List<NoticeFiles>>builder()
                        .success(true)
                        .message("조회된 파일이 없습니다.")
                        .data(files)
                        .build();
                return ResponseEntity.ok(response);
            }

            ApiResponse<List<NoticeFiles>> response = ApiResponse.<List<NoticeFiles>>builder()
                    .success(true)
                    .message("파일 조회 성공")
                    .data(files)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("파일 조회 실패: {}", e.getMessage());
            ApiResponse<List<NoticeFiles>> response = ApiResponse.<List<NoticeFiles>>builder()
                    .success(false)
                    .message("파일 조회 중 오류가 발생했습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 공지사항 파일 다운로드 또는 이미지 반환
     *
     * @param fileId 파일 ID
     * @return ResponseEntity<Resource>
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> getNoticeFile(@PathVariable String fileId) {
        try {
            // 파일 경로 가져오기
            Path filePath = noticeFilesService.getNoticeFilePath(fileId);
            Resource resource = new UrlResource(filePath.toUri());

            // 파일 존재 여부 확인
            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // MIME 타입 설정
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            log.error("파일 다운로드 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
