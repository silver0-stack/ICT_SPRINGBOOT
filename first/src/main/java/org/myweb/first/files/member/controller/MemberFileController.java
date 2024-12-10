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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * 회원 UUID로 프로필 사진 조회
     *
     * @param memUuid 회원 UUID
     * @return MemberFiles DTO
     */
    @GetMapping("/{memUuid}")
    @Operation(summary = "프로필 사진 조회", description = "특정 회원의 프로필 사진을 조회하는 API")
    public ResponseEntity<ApiResponse<MemberFiles>> getMemberFiles(@PathVariable String memUuid){
        MemberFiles memberFiles = memberFilesService.getMemberFileByMemberUuid(memUuid);
        if(memberFiles != null){
            ApiResponse<MemberFiles> response = ApiResponse.<MemberFiles>builder()
                    .success(true)
                    .message("프로필 사진 조회 성공")
                    .data(memberFiles)
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<MemberFiles> response = ApiResponse.<MemberFiles>builder()
                    .success(false)
                    .message("프로필 사진이 존재하지 않습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
    @Operation(summary = "프로필 사진 업로드", description = "특정 회원의 프로필 사진을 업로드하는 API")
    public ResponseEntity<ApiResponse<MemberFiles>> uploadMemberFiles(@PathVariable String memUuid,
                                                                      @RequestParam("photoFile") MultipartFile file){

        // 파일 업로드 처리
        if (file != null && !file.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            // 파일 이름 검증 (예: 허용된 확장자만)
            if (!isValidFileExtension(fileName)) {
                ApiResponse<MemberFiles> response = ApiResponse.<MemberFiles>builder()
                        .success(false)
                        .message("허용되지 않는 파일 형식입니다.")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 오류 응답 반환
            }

            // 파일 이름 재정의 (사용자ID + 원본 파일명)
            String renameFileName = memUuid + "_" + fileName;

            try {
                // 업로드 디렉토리 경로 설정
                Path saveDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
                if (!Files.exists(saveDirectory)) {
                    Files.createDirectories(saveDirectory); // 디렉토리 없을 경우 생성
                }

                // 파일 저장 경로 설정
                Path targetLocation = saveDirectory.resolve(renameFileName);
                // 파일 복사(덮어쓰기 허용)
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                log.info("File uploaded to: {}", targetLocation);
                // 파일 경로 저장 (예: member 객체에 저장)
                // member.setProfilePicturePath(targetLocation.toString()); // 이 라인은 제거됨
                // 프로필 사진은 MemberFilesController에서 별도로 관리됨
            } catch (java.io.IOException e) {
                log.error("File upload failed: {}", e.getMessage(), e); // 파일 업로드 실패
                ApiResponse<MemberFiles> response = ApiResponse.<MemberFiles>builder()
                        .success(false)
                        .message("첨부파일 업로드 실패!")
                        .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 오류 응답 반환
            }
        }

        try {
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
        } catch (Exception e){
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
