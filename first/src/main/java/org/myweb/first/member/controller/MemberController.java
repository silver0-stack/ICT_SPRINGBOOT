package org.myweb.first.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.common.ApiResponse;
import org.myweb.first.common.LoginResponse;
import org.myweb.first.common.util.JwtUtil;
import org.myweb.first.member.model.dto.Member;
import org.myweb.first.member.model.dto.MemberInfoDTO;
import org.myweb.first.member.model.dto.RefreshTokenRequest;
import org.myweb.first.member.model.dto.User;
import org.myweb.first.member.model.service.MemberService;
import org.myweb.first.member.model.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.*;

/*
 * 회원 관련 REST API를 제공하는 컨트롤러 클래스
 * */

@Slf4j //로깅을 위한 SLF4J LOGGER 생성
@RestController // RESTful 컨트롤러로 등록
@RequestMapping("/api/members") // 기본 url 경로 설정
@CrossOrigin(origins = "*") // CORS 설정 (보안을 위해 필요한 대로 설정하기)
@RequiredArgsConstructor // final 필드를 인자로 받는 생성자 생성
@Tag(name = "멤버 컨트롤러 테스트", description = "응답 api 테스트")
public class MemberController {

    private final MemberService memberService; // 회원 서비스
    private final JwtUtil jwtUtil; // JWT 유틸리티 클래스
    private final RefreshTokenService refreshTokenService;// Refresh Token 서비스 클래스

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

        // Optinal<Member>를 반환하여 호출 측에서 명식적으로 처리하도록 유도하기로 했음
        // -> 컨트롤러에서 명시적으로 유도하겠음
        Optional<Member> loginUser = memberService.selectMember(user.getUserId());

        // 회원 정보가 존재하고, 비밀번호가 일치하는지 확인
        if (loginUser.isPresent()
                && StringUtils.hasText(user.getUserPwd())
                && memberService.matchesPassword(user.getUserPwd(), loginUser.get().getUserPwd())) {
            String roles = loginUser.get().getRoles(); //roles 필드 가져오기
            log.debug("User roles: {}", roles);

            //Access Token 생성
            String accessToken = jwtUtil.generateAccessToken(user.getUserId(), roles);

            //Refresh Token 생성
            String refreshToken=jwtUtil.generateRefreshToken(user.getUserId(), roles);

            //Refresh Token 저장
            refreshTokenService.storedRefreshToken(user.getUserId(), refreshToken);
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
     * @param refreshTokenRequeset Refresh Token 요청 DTO
     * @return 새로운 Access Token을 포함한 응답
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        String refreshToken=refreshTokenRequest.getRefreshToken();
        log.info("Refresh Token received: {}", refreshToken);

        if(StringUtils.hasText(refreshToken) && jwtUtil.validateToken(refreshToken)){
            String userId=jwtUtil.extractUserId(refreshToken);
            String roles=jwtUtil.extractRoles(refreshToken);

            // Refresh Token 검증
            if(refreshTokenService.validateRefreshToken(userId, refreshToken)){
                Optional<Member> memberOpt = memberService.selectMember(userId);
                if(memberOpt.isPresent()){
                    String newAccessToken = jwtUtil.generateAccessToken(userId, roles);

                    // 로그인 응답 dto 생성 (Refresh Token은 새로 발급하지 않음
                    LoginResponse loginRespons=LoginResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(refreshToken) // 기존 Refresh Token 유지
                            .member(memberOpt.get())
                            .build();

                    ApiResponse<LoginResponse> response=ApiResponse.<LoginResponse>builder()
                            .success(true)
                            .message("Access Token이 새로 발급되었습니다.")
                            .data(loginRespons)
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
     * @param userId 사용자 ID
     * @return 로그아웃 결과 응답
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestParam String userId){
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
     * @param userId 사용자 ID
     * @return 중복 상태 ("ok": 중복되지 않음, "dup": 중복됨)
     */
    @PostMapping("/idchk")
    @Operation(summary = "ID 중복 체크", description = "유저가 id 중복체크 할 때 사용하는 API")
    public ResponseEntity<ApiResponse<String>> dupCheckIdMethod(@RequestParam("userid") String userId) {
        log.info("ID check for userId: {}", userId); // ID 체크 로그

        // ID 존재 여부 확인
        int userIdCount = memberService.selectCheckId(userId);
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
     * @param mfile  프로필 사진 파일 (선택적)
     * @return 회원가입 결과 응답
     */
    @PostMapping("/enroll")
    @Operation(summary = "회원가입", description = "유저가 회원가입 할 때 사용하는 API")
    public ResponseEntity<ApiResponse<Member>> memberInsertMethod(@ModelAttribute Member member,
                                                                  @RequestParam(name = "photofile", required = false) MultipartFile mfile) {
        log.info("Enrollment attempt: {}", member); //회원가입 시도 로그

        // 비밀번호 암호화
        if (StringUtils.hasText(member.getUserPwd())) {
            member.setUserPwd(memberService.encodedPassword(member.getUserPwd()));
            log.info("Encoded password: {}", member.getUserPwd());
        }

        // 파일 업로드 처리
        if (mfile != null && !mfile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mfile.getOriginalFilename()));

            // 파일 이름 검증 (예: 허용된 확장자만)
            if (!isValidFileExtension(fileName)) {
                ApiResponse<Member> response = ApiResponse.<Member>builder()
                        .success(false)
                        .message("허용되지 않는 파일 형식입니다.")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 오류 응답 반환
            }

            // 파일 이름 재정의 (사용자ID + 원본 파일명)
            String renameFileName = member.getUserId() + "_" + fileName;

            try {
                // 업로드 디렉토리 경로 설정
                Path saveDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
                if (!Files.exists(saveDirectory)) {
                    Files.createDirectories(saveDirectory); // 디렉토리 없을 경우 생성
                }

                // 파일 저장 경로 설정
                Path targetLocation = saveDirectory.resolve(renameFileName);
                // 파일 복사(덮어쓰기 허용)
                Files.copy(mfile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                // DTO에 저장된 파일명 설정
                member.setPhotoFileName(renameFileName);
            } catch (IOException e) {
                log.error("File upload failed: {}", e.getMessage(), e); // 파일 업로드 실패
                ApiResponse<Member> response = ApiResponse.<Member>builder()
                        .success(false)
                        .message("첨부파일 업로드 실패!")
                        .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 오류 응답 반환
            }
        }

        // 기본값 설정
        member.setLoginOk("Y"); // 로그인 허용
        member.setAdminYN("N"); // 관리자 여부: N
        member.setSignType("direct"); // 가입 방식: direct


        // 역할 할당
        if ("Y".equalsIgnoreCase(member.getAdminYN())) {
            member.setRoles("ADMIN"); // 관리자 역할 설정
        } else {
            member.setRoles("USER"); // 일반 사용자 역할 설정
        }
        log.info("Final member data: {}", member); // 최종 회원 데이터 로그

        // 회원가입 처리
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
     * '내 정보 보기' 처리 메소드
     *
     * @param userId 사용자 ID
     * @return 회원 정보 응답
     */
    /* userPwd(비밀번호 해시)는 절대 클라이언트에 노출되어서는 안 됨, 데이터 유출 시 큰 보안 사고임
     * 따라서 userPwd만 없는 새로운 MemberInfoDTO 클래스를 생성해서 반환함*/
    @GetMapping("/{userId}")
    @Operation(summary = "회원 조회", description = "유저가 로그인 후 회원 조회하는 API")
    public ResponseEntity<ApiResponse<MemberInfoDTO>> memberDetailMethod(@PathVariable("userId") String userId) {
        log.info("Fetching member info for userId: {}", userId); // 회원 정보 조회 시도 로그

        // 사용자 ID로 회원 정보 조회
        Optional<Member> memberOpt = memberService.selectMember(userId);

        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();

            // 원본 파일 이름 추출 (사용자 ID 제거)
            String originalFilename = null;
            if (member.getPhotoFileName() != null) {
                int underscoreIndex = member.getPhotoFileName().indexOf('_');
                originalFilename = underscoreIndex != -1 ? member.getPhotoFileName().substring(underscoreIndex + 1) : member.getPhotoFileName();
            }
            member.setPhotoFileName(originalFilename); // 조회할 DTO에 원본 파일명 설정

            // MemberInfoDTO 로 변환(비밀번호 제외)
            MemberInfoDTO memberInfoDTO = new MemberInfoDTO(
                    member.getUserId(),
                    member.getUserName(),
                    member.getGender(),
                    member.getAge(),
                    member.getPhone(),
                    member.getEmail(),
                    member.getEnrollDate(),
                    member.getLastModified(),
                    member.getSignType(),
                    member.getAdminYN(),
                    member.getLoginOk(),
                    member.getPhotoFileName(),
                    member.getRoles()
            );

            // 성공 응답 생성
            ApiResponse<MemberInfoDTO> response = ApiResponse.<MemberInfoDTO>builder()
                    .success(true)
                    .message("회원 정보 조회 성공")
                    .data(memberInfoDTO)
                    .build();
            return ResponseEntity.ok(response); // 성공 응답 반환
        } else {
            // 실패 응답 생성
            ApiResponse<MemberInfoDTO> response = ApiResponse.<MemberInfoDTO>builder()
                    .success(false)
                    .message(userId + "에 대한 회원 정보 조회 실패!")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 실패 응답 반환
        }
    }


    /**
     * 회원 정보 수정 처리 메소드
     *
     * @param userId 사용자 ID
     * @param member 수정할 회원 정보 DTO
     * @param mfile  프로필 사진 파일 (선택적)
     * @return 수정 결과 응답
     */
    // 회원 정보 수정 처리
    @PutMapping("/{userId}")
    @Operation(summary = "회원 수정", description = "유저가 회원 정보 수정할 때 사용하는 API")
    public ResponseEntity<ApiResponse<Member>> memberUpdateMethod(@PathVariable("userId") String userId,
                                                                  @ModelAttribute Member member,
                                                                  @RequestParam(name = "photofile", required = false) MultipartFile mfile) {
        log.info("Member update attempt: {}", member); // 회원 정보 수정 시도 로그

        // 기존 회원 정보 조회
        Optional<Member> existingMemberOpt = memberService.selectMember(userId);
        if (existingMemberOpt.isEmpty()) {
            // 회원 정보가 없을 경우 실패 응답 반환
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(false)
                    .message("회원 정보가 존재하지 않습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Member existingMember = existingMemberOpt.get();

        // 비밀번호 변경 여부 확인 및 엄호화
        if (StringUtils.hasText(member.getUserPwd())) {
            existingMember.setUserPwd(memberService.encodedPassword(member.getUserPwd()));
            log.info("Encoded new password: {}, length: {} for userId: {}", member.getUserPwd(), member.getUserPwd().length(), userId);
        }

        // 파일 업로드 처리
        if (mfile != null && !mfile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mfile.getOriginalFilename()));

            // 파일 이름 검증(예: 허용된 확장자만)
            if (!isValidFileExtension(fileName)) {
                ApiResponse<Member> response = ApiResponse.<Member>builder()
                        .success(false)
                        .message("허용되지 않는 파일 형식입니다.")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 오류 응답 반환
            }

            // 기존 파일명과 다른 경우에만 파일 저장
            if (!fileName.equals(existingMember.getPhotoFileName())) {
                String renameFileName = member.getUserId() + "_" + fileName;

                try {
                    // 업로드 디렉토리 경로 설정
                    Path saveDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
                    if (!Files.exists(saveDirectory)) {
                        Files.createDirectories(saveDirectory); // 디렉토리 없을 경우 생성
                    }
                    // 파일 저장 경로 설정
                    Path targetLocation = saveDirectory.resolve(renameFileName);
                    // 파일 복사 (덮어쓰기 허용)
                    Files.copy(mfile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                    // DTO에 저장된 파일명 설정
                    existingMember.setPhotoFileName(renameFileName);
                } catch (IOException e) {
                    log.error("File upload failed: {}", e.getMessage(), e); //파일 업로드 실패 로그
                    ApiResponse<Member> response = ApiResponse.<Member>builder()
                            .success(false)
                            .message("첨부파일 업로드 실패")
                            .build();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 오류 응답 반환
                }
            }

        } else {
            // 파일이 없는 경우 기존 파일명 유지
            existingMember.setPhotoFileName(userId + "_" + existingMember.getPhotoFileName());
        }

        // 기타 필드 수정
        existingMember.setLastModified(new Date(System.currentTimeMillis())); //최종 수정일
        existingMember.setAdminYN(member.getAdminYN()); //관리자 여부 업데이트
        existingMember.setLoginOk(member.getLoginOk()); // 로그인 허용 여부 업데이트
        existingMember.setSignType(member.getSignType()); // 가입 방식 업데이트

        //회원 정보 수정 처리
        int result = memberService.updateMember(existingMember);
        if (result > 0) {
            // 성공 응답 생성
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(true)
                    .message("회원 정보 수정 완료")
                    .build();
            return ResponseEntity.ok(response); // 성공 응답 반환
        } else {
            // 실패 응답 생성
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(false)
                    .message("회원 정보 수정 실패! 확인하고 다시 시도해주세요.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 실패 응답 반환
        }
    }

    /**
     * 회원 삭제 처리 메소드
     *
     * @param userId 사용자 ID
     * @return 삭제 결과 응답
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 삭제", description = "회원 탈퇴할 때 사용하는 API")
    public ResponseEntity<ApiResponse<String>> memberDeleteMethod(@PathVariable("userId") String userId) {
        log.info("Deleting member with userId: {}", userId); // 회원 삭제 시도 로그

        // 회원 삭제 처리
        int result = memberService.deleteMember(userId);
        if (result > 0) {
            // 성공 응답 생성
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(true)
                    .message("회원 탈퇴가 완료되었습니다.")
                    .data("삭제된 userId: " + userId)
                    .build();
            return ResponseEntity.ok(response); // 성공 응답 반환
        } else {
            // 실패 응답 생성
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(false)
                    .message(userId + "님의 회원 탈퇴 실패! 관리자에게 문의하세요. ")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 실패 응답 반환
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
    @Operation(summary = "관리자의 회원목록 조회", description = "관리자가 회원의 목록을 조회할 때 사용하는 API")
    public ResponseEntity<ApiResponse<Page<Member>>> memberListMethod(
            @RequestParam(name = "page", defaultValue = "1") int currentPage,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "sort", defaultValue = "enrollDate,desc") String[] sort) {
        log.info("Fetching member list: page {}, limit {}, sort {}", currentPage, limit, sort); // 회원 목록 조회 로그


        // 정렬 처리
        Sort.Direction direction = Sort.Direction.DESC; //기본 정렬 방향: 내림차순
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

    /**
     * 로그인 제한/허용 상태 변경 메소드
     *
     * @param member 수정할 회원 정보 DTO
     * @param userId 사용자 ID
     * @return 변경 결과 응답
     */
    @PutMapping("/{userId}/loginok")
    @Operation(summary = "로그인 상태 변경", description = "관리자가 로그인 상태 제한/허용 상태 변경할 때 사용하는 API")
    public ResponseEntity<ApiResponse<Member>> changeLoginOKMethod(@RequestBody Member member, @PathVariable String userId) {

        log.info("Updating login OK for userId: {}", userId); // 로그인 제한/허용 상태 변경 로그

        member.setUserId(userId); // URL에서 받은 userId를 DTO에 설정

        // 로그인 허용 상태 업데이트
        int result = memberService.updateLoginOK(member);


        if (result > 0) {
            // 수정된 회원 정보 조회
            Optional<Member> updatedMemberOpt = memberService.selectMember(userId);
            if (updatedMemberOpt.isPresent()) {
                // 성공 응답 생성
                ApiResponse<Member> response = ApiResponse.<Member>builder()
                        .success(true)
                        .message("로그인 제한/허용 상태가 변경되었습니다.")
                        .data(updatedMemberOpt.get())
                        .build();
                return ResponseEntity.ok(response); // 성공 응답 반환
            }
        }

        // 실패 응답 생성
        ApiResponse<Member> response = ApiResponse.<Member>builder()
                .success(false)
                .message("로그인 제한/허용 처리에 실패했습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 실패 응답 반환

    }

    /**
     * 관리자용 회원 검색 기능 메소드
     *
     * @param action      검색 기준 (id, gender, age, enrollDate, loginok)
     * @param keyword     검색 키워드
     * @param beginStr    검색 시작 날짜 (enrollDate 기준)
     * @param endStr      검색 종료 날짜 (enrollDate 기준)
     * @param currentPage 현재 페이지 번호 (기본값: 1)
     * @param limit       페이지당 회원 수 (기본값: 10)
     * @param sort        정렬 기준 (기본값: enrollDate,desc)
     * @return 검색 결과 응답
     */
    @GetMapping("/search")
    @Operation(summary = "멤버 필터링", description = "관리자 용 회원 검색할 때 사용하는 API")
    public ResponseEntity<ApiResponse<Page<Member>>> memberSearchMethod(@RequestParam("action") String action,
                                                                        @RequestParam(value = "keyword", required = false) String keyword,
                                                                        @RequestParam(value = "begin", required = false) String beginStr,
                                                                        @RequestParam(value = "end", required = false) String endStr,
                                                                        @RequestParam(value = "page", defaultValue = "1") int currentPage,
                                                                        @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                                        @RequestParam(name = "sort", defaultValue = "enrollDate,desc") String[] sort) {


        log.info("Member search: action={}, keyword={}, begin={}, end={}, page={}, limit={}, sort={}",
                action, keyword, beginStr, endStr, currentPage, limit, sort); // 검색 요청 로그


        // 정렬 처리
        Sort.Direction direction = Sort.Direction.DESC; // 기본 정렬 방향: 내림차순
        String sortBy = "enrollDate"; // 기본 정렬 필드: 가입일

        if (sort.length == 2) {
            sortBy = sort[0]; // 정렬 기준 필드
            direction = sort[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC; // 정렬 방향 설정
        }

        // Pageable 객체 생성: 페이징 및 정렬 설정
        Pageable pageable = PageRequest.of(currentPage - 1, limit, Sort.by(direction, sortBy));

        // 검색 결과 조회
        /*서비스 호출 및 Page 객체 반환
         * searchMembers 메소드는 검색 조건에 따라 필터링된 회원 목록을 페이징하여 반환
         * */
        Page<Member> searchResult = memberService.searchMembers(action, keyword, beginStr, endStr, pageable);

        // Page 객체의 hasContent() : 반환된 데이터 리스트가 있을 시 (boolean)
        if (searchResult.hasContent()) {
            // 성공 응답 생성
            ApiResponse<Page<Member>> response = ApiResponse.<Page<Member>>builder()
                    .success(true)
                    .message("검색 결과 조회 성공")
                    .data(searchResult)
                    .build();
            return ResponseEntity.ok(response); // 성공 응답 반환
        } else {
            // 검색 결과 없음 응답 생성
            ApiResponse<Page<Member>> response = ApiResponse.<Page<Member>>builder()
                    .success(false)
                    .message("검색 결과가 존재하지 않습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // 400 Not Found 반환
        }


    }
}
