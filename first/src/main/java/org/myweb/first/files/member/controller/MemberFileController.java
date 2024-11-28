package org.myweb.first.files.member.controller;

import lombok.RequiredArgsConstructor;
import org.myweb.first.common.ApiResponse;
import org.myweb.first.files.member.model.dto.MemberFiles;
import org.myweb.first.files.member.model.service.MemberFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/profile-pictures")
@RequiredArgsConstructor
public class MemberFileController {
    @Autowired
    private MemberFilesService memberFileService;


    /**
     * 회원 UUID로 프로필 사진 조회
     * @param memUuid 회원 UUID
     * @return MemberFiles DTO
     */
    @GetMapping("/{memUuid}")
    public ResponseEntity<ApiResponse<MemberFiles>> getMemberFiles(@PathVariable String memUuid){
        MemberFiles memberFiles = memberFileService.getMemberFileByMemberUuid(memUuid);
        if(memberFiles != null){
            ApiResponse<MemberFiles> response = ApiResponse.<MemberFiles>builder()
                    .success(true)
                    .message("프로필 사진 조회 성공")
                    .data(memberFiles)
                    .build();
            return ResponseEntity.ok(response);
        }else{
            ApiResponse<MemberFiles> response = ApiResponse.<MemberFiles>builder()
                    .success(false)
                    .message("프로필 사진이 존재하지 않습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    /**
     * 프로필 사진 업로드
     * @param memUuid 회원 UUID
     * @param file 업로드할 파일
     * @return MemberFiles DTO
     */
    @PostMapping("/{memUuid}")
    public ResponseEntity<ApiResponse<MemberFiles>> uploadMemberFiles(@PathVariable String memUuid,
                                                                      @RequestParam("file") MultipartFile file){
        try {
            int result = memberFileService.uploadMemberFiles(memUuid, file);
            if(result > 0){
                ApiResponse<MemberFiles> response = ApiResponse.<MemberFiles>builder()
                        .success(true)
                        .message("프로필 사진 업로드 성공")
                        .build();
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }else{
                ApiResponse<MemberFiles> response = ApiResponse.<MemberFiles>builder()
                        .success(false)
                        .message("프로필 사진 업로드 실패! 다시 시도해 주세요.")
                        .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
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
        }

    }


    /**
     * 프로필 사진 삭제
     * @param memUuid 회원 UUID
     * @return 성공 여부(0, 1)
     */
    @DeleteMapping("/{memUuid}")
    public ResponseEntity<ApiResponse<Void>> deleteProfilePicture(@PathVariable String memUuid) {
        int result = memberFileService.deleteMemberFile(memUuid);
        if (result>0) {
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
    }



}
