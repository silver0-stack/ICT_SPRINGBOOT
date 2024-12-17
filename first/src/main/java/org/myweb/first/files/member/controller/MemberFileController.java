package org.myweb.first.files.member.controller;

import io.jsonwebtoken.io.IOException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.common.ApiResponse;
import org.myweb.first.files.member.model.dto.MemberFiles;
import org.myweb.first.files.member.model.service.MemberFilesService;
import org.myweb.first.member.model.dto.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;


@RestController
@Slf4j
@RequestMapping("/api/profile-pictures")
@RequiredArgsConstructor
public class MemberFileController {
    private final MemberFilesService memberFilesService;
    @Value("${file.upload-dir}") // 파일 업로드 디렉토리 경로 주입
    private String uploadDir;

    /**
     * 회원 UUID로 프사 이미지 반환용 메소드
     *
     * @param memUuid 회원 UUID
     * @return MemberFiles DTO
     */
    @GetMapping("/{memUuid}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String memUuid) {
        try {
            // 1. 서비스에서 파일 경로 가져오기
            // Java NIO(Non-blocking I/O)에서 제공하는 클래스, 파일이나 디렉토리의 경로를 나타냄
            // 서버에 저장딘 프로필 사진 파일의 위치
            // 예: ./uploads/profile123.jpg
            Path filePath = memberFilesService.getProfilePicturePath(memUuid);
            // Resource는 Spring Framework에서 파일이나 URL 등을 추상화한 객체
            // UrlResource는 특정 URL에 있는 리소스를 다루는 클래스
            // 여기선 filePath.toUri()를 사용해 파일 경로를 URL 형식으로 변환한 후 리소스로 읽어온다
            // file:///./uploads/profile123.jpg
            Resource fileResource = new UrlResource(filePath.toUri());

            // 파일이 실제로 존재하는지 확인
            if (!fileResource.exists()) {
                // 파일이 없으면 404 NOT FOUND 상태로 응답을 반환
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 적절한 MIME 타입 설정
            // Files.probeContentType(Path path)는 파일을 MIME 타입을 반환하는 메소드
            // MIME 타입: 파일 형식 정보를 나타내는 문자열
            // 예) image/jpeg, image/png, text/html
            // 반환값이 null 인 경우 파일 형식을 알 수 없다는 의미다.
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                // application/octet-stream은 MIME 타입의 기본값으로
                // 알 수 없는 파일 형식을 나타낸다.
                // 브라우저는 이 타입을 다운로드 가능한 파일로 인식한다
                // 여기서는 MIME 타입이 null일 때 기본값으로 설정된다.
                contentType = "application/octet-stream";
            }

            return ResponseEntity
                    .ok() // HTTP 상태 코드 200(성공)
                    .contentType(MediaType.parseMediaType(contentType)) // 응답의 Content-Type 헤더 설정
                    .body(fileResource); // 응답 본문에 파일 리소스를 포함시킨다.

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    /**
     * 파일 확장자 검증 메소드
     *
     * @param fileName 파일 이름
     * @return 유효한 확장자 여부
     */
    private boolean isValidFileExtension(String fileName) {
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif"}; // 허용된 확장자 목록
        for (String ext : allowedExtensions) {
            if (fileName.toLowerCase().endsWith(ext)) {
                return true; // 유효한 확장자일 경우
            }
        }
        return false; // 유효하지 않은 확장자일 경우
    }


    /**
     * 프로필 사진 업로드
     *
     * @param memUuid 회원 UUID
     * @param file 업로드할 파일
     * @return MemberFiles DTO
     */
    @PostMapping("/{memUuid}")
    public ResponseEntity<ApiResponse<MemberFiles>> uploadMemberFiles(
            @PathVariable String memUuid,
            @RequestParam("photoFile") MultipartFile file) {

        try {
            // 파일 업로드 서비스 호출
            MemberFiles memberFiles = memberFilesService.uploadMemberFiles(memUuid, file);

            ApiResponse<MemberFiles> response = ApiResponse.<MemberFiles>builder()
                    .success(true)
                    .message("프로필 사진 업로드 성공")
                    .data(memberFiles)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            ApiResponse<MemberFiles> response = ApiResponse.<MemberFiles>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (IOException e) {
            ApiResponse<MemberFiles> response = ApiResponse.<MemberFiles>builder()
                    .success(false)
                    .message("파일 업로드 중 오류가 발생했습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        } catch (Exception e) {
            log.error("예기치 못한 오류 발생: {}", e.getMessage(), e);
            ApiResponse<MemberFiles> response = ApiResponse.<MemberFiles>builder()
                    .success(false)
                    .message("프로필 사진 업로드 중 예기치 않은 오류가 발생했습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    /**
     * 프로필 사진 삭제
     *
     * @param memUuid 회원 UUID
     * @return 성공 여부 (1: 성공, 0: 실패)
     */
    @DeleteMapping("/{memUuid}")
    @Operation(summary = "프로필 사진 삭제", description = "특정 회원의 프로필 사진을 삭제하는 API")
    public ResponseEntity<ApiResponse<Void>> deleteProfilePicture(@PathVariable String memUuid) {
        try {
            int result = memberFilesService.deleteMemberFile(memUuid);
            if(result > 0){
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                        .success(true)
                        .message("프로필 사진 삭제 성공")
                        .build();
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                        .success(false)
                        .message("프로필 사진이 존재하지 않습니다.")
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (IOException e){
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .message("프로필 사진 삭제 중 오류가 발생했습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e){
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .message("프로필 사진 삭제 중 예기치 않은 오류가 발생했습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
