package org.myweb.first.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.common.ApiResponse;
import org.myweb.first.common.LoginResponse;
import org.myweb.first.common.util.JwtUtil;
import org.myweb.first.member.model.dto.*;
import org.myweb.first.member.model.service.MemberService;
import org.myweb.first.member.model.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/*
 * 회원 관련 REST API를 제공하는 컨트롤러 클래스
 */

@Slf4j // 로깅을 위한 SLF4J LOGGER 생성
@RestController // RESTful 컨트롤러로 등록
@RequestMapping("/api/members") // 기본 URL 경로 설정
@CrossOrigin(origins = "*") // CORS 설정 (보안을 위해 필요한 대로 설정하기)
@RequiredArgsConstructor // final 필드를 인자로 받는 생성자 생성
@Tag(name = "멤버 컨트롤러", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService; // 회원 서비스
    private final JwtUtil jwtUtil; // JWT 유틸리티 클래스
    private final RefreshTokenService refreshTokenService; // Refresh Token 서비스 클래스

    @Value("${file.upload-dir}") // 파일 업로드 디렉토리 경로 주입
    private String uploadDir;

    /**
     * 로그인 처리 메소드
     *
     * @param user 로그인 요청 데이터 (User DTO)
     * @return 로그인 결과 응답
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "유저가 로그인 할 때 사용하는 API")
    public ResponseEntity<ApiResponse<LoginResponse>> loginMethod(@RequestBody User user) {
        log.info("Login attempt: {}", user); // 로그인 시도 로그

        Optional<Member> loginUser = memberService.selectMember(user.getMemId());

        // 회원 정보가 존재하고, 비밀번호가 일치하는지 확인
        if (loginUser.isPresent()
                && StringUtils.hasText(user.getMemPw())
                && memberService.matchesPassword(user.getMemPw(), loginUser.get().getMemPw())) {
            String memType = loginUser.get().getMemType(); // memType 필드 가져오기
            log.debug("User memType: {}", memType);

            // Access Token 생성
            String accessToken = jwtUtil.generateAccessToken(loginUser.get().getMemUuid(), memType);

            // Refresh Token 생성
            String refreshToken = jwtUtil.generateRefreshToken(loginUser.get().getMemUuid(), memType);

            // Refresh Token 저장
            refreshTokenService.storedRefreshToken(loginUser.get().getMemUuid(), refreshToken);

            // 로그인 응답 DTO 생성
            LoginResponse loginResponse = LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .member(loginUser.get())
                    .build();

            // API 응답 생성
            ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                    .success(true)
                    .message("로그인 성공")
                    .data(loginResponse)
                    .build();

            return ResponseEntity.ok(response); // 성공 응답 반환
        } else {
            // 로그인 실패 응답 생성
            ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                    .success(false)
                    .message("로그인 실패! 아이디나 암호를 확인하세요.")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 실패 응답 반환
        }
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token 발급
     *
     * @param refreshTokenRequest Refresh Token 요청 DTO
     * @return 새로운 Access Token을 포함한 응답
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh Token으로 Access Token 재발급", description = "유효한 Refresh Token을 사용하여 새로운 Access Token을 발급합니다.")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        log.info("Refresh Token received: {}", refreshToken);

        if (StringUtils.hasText(refreshToken) && jwtUtil.validateToken(refreshToken)) {
            String userId = jwtUtil.extractUserId(refreshToken);
            String memType = jwtUtil.extractRoles(refreshToken);

            // Refresh Token 검증
            if (refreshTokenService.validateRefreshToken(userId, refreshToken)) {
                Optional<Member> memberOpt = memberService.selectMember(userId);
                if (memberOpt.isPresent()) {
                    String newAccessToken = jwtUtil.generateAccessToken(userId, memType);

                    // 로그인 응답 DTO 생성 (Refresh Token은 새로 발급하지 않음)
                    LoginResponse loginResponse = LoginResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(refreshToken) // 기존 Refresh Token 유지
                            .member(memberOpt.get())
                            .build();

                    ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                            .success(true)
                            .message("Access Token이 새로 발급되었습니다.")
                            .data(loginResponse)
                            .build();

                    return ResponseEntity.ok(response);
                }
            }
        }

        // Refresh Token 검증 실패 또는 사용자 정보 없음
        ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                .success(false)
                .message("유효하지 않은 Refresh Token 입니다.")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 사용자 로그아웃 요청 처리
     *
     * @param userId 사용자 ID
     * @return 로그아웃 결과 응답
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestParam String userId) {
        log.info("Logout attempt for user: {}", userId);

        // Refresh Token 삭제
        refreshTokenService.deleteRefreshToken(userId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("로그아웃 성공")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * ID 중복 검사 메소드
     *
     * @param memId 사용자 ID
     * @return 중복 상태 ("ok": 중복되지 않음, "dup": 중복됨)
     */
    @PostMapping("/idchk")
    @Operation(summary = "ID 중복 체크", description = "회원 가입 시 ID 중복 여부를 확인하는 API")
    public ResponseEntity<ApiResponse<String>> dupCheckIdMethod(@RequestParam("memId") String memId) {
        log.info("ID check for memId: {}", memId); // ID 체크 로그

        // ID 존재 여부 확인
        int userIdCount = memberService.selectCheckId(memId);
        String status = (userIdCount == 0) ? "ok" : "dup";

        // API 응답 생성
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("ID 중복 검사 완료")
                .data(status)
                .build();
        return ResponseEntity.ok(response); // 응답 반환
    }

    /**
     * 회원가입 처리 메소드
     *
     * @param member 회원 정보 DTO (폼 데이터)
     * @param bindingResult 유효성 검증 결과
     * @return 회원가입 결과 응답
     */
    @PostMapping("/enroll")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록하는 API")
    public ResponseEntity<ApiResponse<Member>> createMember(@Valid @ModelAttribute Member member,
                                                            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Member>builder().success(false).message(errorMessage).build());
        }
        log.info("Enrollment attempt: {}", member); // 회원가입 시도 로그


        log.info("Final member data: {}", member); // 최종 회원 데이터 로그

        // 회원가입 처리
        // 엔터티에서 @PrePersist로 대체
        //member.setMemUuid(UUID.randomUUID().toString());
        int result = memberService.insertMember(member);
        if (result > 0) {
            // 성공 응답 생성
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(true)
                    .message("회원가입이 완료되었습니다.")
                    .data(member)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // 성공 응답 반환
        } else {
            // 실패 응답 생성
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(false)
                    .message("회원 가입 실패! 확인하고 다시 가입해 주세요.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 실패 응답 반환
        }
    }

    /**
     * '내 정보 보기' 처리 메소드
     *
     * @param memId 사용자 ID
     * @return 회원 정보 응답
     */
    /* userPwd(비밀번호 해시)는 절대 클라이언트에 노출되어서는 안 됨, 데이터 유출 시 큰 보안 사고임
     * 따라서 userPwd만 없는 새로운 MemberInfoDTO 클래스를 생성해서 반환함*/
    @GetMapping("/{memId}")
    @Operation(summary = "회원 정보 조회", description = "특정 회원의 정보를 조회하는 API")
    public ResponseEntity<ApiResponse<Member>> getMember(@PathVariable String memId) {
        Optional<Member> memberOpt = memberService.selectMember(memId);
        if (memberOpt.isPresent()) {
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(true)
                    .message("회원 조회 성공")
                    .data(memberOpt.get())
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(false)
                    .message("회원이 존재하지 않습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 회원 정보 수정 처리 메소드
     *
     * @param memId 사용자 ID
     * @param member 수정할 회원 정보 DTO
     * @return 수정 결과 응답
     */
    @PutMapping("/{memId}")
    @Operation(summary = "회원 정보 수정", description = "회원의 정보를 수정하는 API")
    @PreAuthorize("hasAuthority('ADMIN') or #memId == authentication.principal.memId") // 권한 설정 (ADMIN 또는 자기 자신)
    public ResponseEntity<ApiResponse<Member>> memberUpdateMethod(@PathVariable String memId,
                                                                  @Valid @ModelAttribute Member member,
                                                                  BindingResult bindingResult) {
        try {
            log.info("Member update attempt: {}", member); // 회원 정보 수정 시도 로그

            // 경로의 memId와 요청 본문의 memId가 일치하는지 확인
            if (!memId.equals(member.getMemId())) {
                // memId가 일치하지 않을 경우 실패 응답 반환
                ApiResponse<Member> response = ApiResponse.<Member>builder()
                        .success(false)
                        .message("요청된 memId와 요청 본문의 memId가 일치하지 않습니다.")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(", "));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.<Member>builder().success(false).message(errorMessage).build());
            }

            // 기존 회원 정보 조회
            Optional<Member> existingMemberOpt = memberService.selectMember(memId);
            if (existingMemberOpt.isEmpty()) {
                // 회원 정보가 없을 경우 실패 응답 반환
                ApiResponse<Member> response = ApiResponse.<Member>builder()
                        .success(false)
                        .message("회원 정보가 존재하지 않습니다.")
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Member existingMember = existingMemberOpt.get();

            // 비밀번호 변경 여부 확인 및 암호화
            if (StringUtils.hasText(member.getMemPw())) {
                existingMember.setMemPw(memberService.encodedPassword(member.getMemPw()));
                log.info("Encoded new password for user ID: {}", memId);
            }

            // 회원 정보 수정 처리
            int result = memberService.updateMember(existingMember);
            if (result > 0) {
                Optional<Member> updatedMemberOpt = memberService.selectMember(memId);
                if (updatedMemberOpt.isPresent()) {
                    ApiResponse<Member> response = ApiResponse.<Member>builder()
                            .success(true)
                            .message("회원 정보 수정 완료")
                            .data(updatedMemberOpt.get())
                            .build();
                    return ResponseEntity.ok(response);
                }
            } else {
                // 실패 응답 생성
                ApiResponse<Member> response = ApiResponse.<Member>builder()
                        .success(false)
                        .message("회원 정보 수정 실패! 확인하고 다시 시도해주세요.")
                        .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 실패 응답 반환
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(false)
                    .message("회원 정보 수정 중 예기치 않은 오류가 발생했습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return null;
    }

    /**
     * 회원 삭제
     *
     * @param memUuid 회원 UUID
     * @return 처리 결과 (1: 성공, 0: 실패)
     */
    @DeleteMapping("/{memUuid}")
    @Operation(summary = "회원 삭제", description = "특정 회원을 삭제하는 API")
    @PreAuthorize("hasAuthority('ADMIN')") // 관리자만 삭제 가능
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable String memUuid){
        try {
            int result = memberService.deleteMember(memUuid);
            if(result > 0){
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                        .success(true)
                        .message("회원 삭제 성공")
                        .build();
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                        .success(false)
                        .message("회원 삭제 실패! 존재하지 않는 회원입니다.")
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e){
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .message("회원 삭제 중 예기치 않은 오류가 발생했습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 회원 목록 조회 메소드 (관리자용)
     *
     * @param currentPage 현재 페이지 번호 (기본값: 1)
     * @param limit       페이지당 항목 수(회원 수) (기본값: 10)
     * @param sort        정렬 기준 (기본값: enrollDate,desc)
     * @return 회원 목록 응답
     */
    @GetMapping
    @Operation(summary = "회원 목록 조회", description = "관리자가 전체 회원 목록을 조회하는 API")
    @PreAuthorize("hasAuthority('ADMIN')") // 관리자만 접근 가능
    public ResponseEntity<ApiResponse<Page<Member>>> memberListMethod(
            @RequestParam(name = "page", defaultValue = "1") int currentPage,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "sort", defaultValue = "enrollDate,desc") String[] sort) {
        log.info("Fetching member list: page {}, limit {}, sort {}", currentPage, limit, sort); // 회원 목록 조회 로그

        // 정렬 처리
        Sort.Direction direction = Sort.Direction.DESC; // 기본 정렬 방향: 내림차순
        String sortBy = "enrollDate"; // 기본 정렬 필드: 가입일

        /*
         * sort 배열의 첫 번째 요소는 정렬할 필드(enrollDate)
         *             두 번째 요소는 정렬 방향(desc 또는 asc)
         */
        if (sort.length == 2) {
            sortBy = sort[0]; // 정렬 기준 필드
            direction = sort[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC; // 정렬 방향 설정
        }

        // Pageable 객체 생성
        // PageRequest는 0부터 페이지 번호를 시작하므로, 클라이언트에서 1부터 시작하는 페이지 번호를 0부터로 변환
        Pageable pageable = PageRequest.of(currentPage - 1, limit, Sort.by(direction, sortBy));
        // 전체 회원 조회
        /*
         * 서비스 호출 및 Page 객체 반환
         * Page<Member>는 페이징된 회원 목록과 메타데이터를 포함
         */
        Page<Member> pageResult = memberService.getAllMembers(pageable);

        // API 응답 생성
        /*클라이언트는 Page<Member> 객체를 통해 데이터 리스트와 페이징 정보를 받을 수 있음*/
        ApiResponse<Page<Member>> response = ApiResponse.<Page<Member>>builder()
                .success(true)
                .message("회원 목록 조회 성공")
                .data(pageResult)
                .build();

        return ResponseEntity.ok(response); // 성공 응답 반환
    }


}
