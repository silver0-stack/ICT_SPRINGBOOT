package org.myweb.first.member.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.common.ApiResponse;
import org.myweb.first.common.LoginResponse;
import org.myweb.first.common.Paging;
import org.myweb.first.common.util.JwtUtil;
import org.myweb.first.member.model.dto.Member;
import org.myweb.first.member.model.dto.MemberInfoDTO;
import org.myweb.first.member.model.dto.User;
import org.myweb.first.member.model.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.*;

// ...

@Slf4j
@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*") // 보안을 위해 필요한 대로 설정하기
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @Value("${file.upload-dir}")
    private String uploadDir;


    // 로그인 페이지 이동
    @GetMapping("/loginPage")
    public String moveLoginPage() {
        return "member/loginPage";
    }

    // 회원가입 페이지 이동
    @GetMapping("/enrollPage")
    public String moveEnrollPage() {
        return "member/enrollPage";
    }

    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>>  loginMethod(@RequestBody User user) {
        log.info("Login attempt: {}", user);

        // Optinal<Member>를 반환하여 호출 측에서 명식적으로 처리하도록 유도하기로 했음
        // -> 컨트롤러에서 명시적으로 유도하겠음
        Optional<Member> loginUser = memberService.selectMember(user.getUserId());

        if (loginUser.isPresent()
                && StringUtils.hasText(user.getUserPwd())
                && memberService.matchesPassword(user.getUserPwd(), loginUser.get().getUserPwd())) {


            String roles = loginUser.get().getRoles(); //roles 필드 가져오기
            log.debug("User roles: {}", roles);
            String token = jwtUtil.generateToken(user.getUserId(), roles);

            
            LoginResponse loginResponse = LoginResponse.builder()
                    .token(token)
                    .member(loginUser.get())
                    .build();

            ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                    .success(true)
                    .message("로그인 성공")
                    .data(loginResponse)
                    .build();

            return ResponseEntity.ok(response);
            //return "common/main";
        } else {
            ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                    .success(false)
                    .message("로그인 실패! 아이디나 암호를 확인하세요.")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//			return "common/error";
        }
    }


	/*
	로그아웃 처리
	RESTful API에서는 클라이언트 측에서 JWT를 삭제함으로써 로그아웃을 처리한다.
	따라서 서버 측에서는 별도의 로그아웃 엔드포인트가 필요하지 않는다.
	하지만, 토큰 블랙리스트를 사용하는 경우 별도의 엔드포인트를 구현할 수 있다.
	*/
    // 로그아웃 처리
//	@GetMapping("/logout")
//	public String logoutMethod(Model model) {
//		// 세션 무효화는 스프링 시큐리티 등에서 처리하는 것이 일반적입니다.
//		// 여기서는 수동으로 처리하고 있습니다.
//		return "redirect:/main";
//	}

    // ID 중복 검사
    @PostMapping("/idchk")
    public ResponseEntity<ApiResponse<String>> dupCheckIdMethod(@RequestParam("userid") String userId) {
        log.info("ID check for userId: {}", userId);

        int userIdCount = memberService.selectCheckId(userId);
        String status = (userIdCount == 0) ? "ok" : "dup";

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("ID 중복 검사 완료")
                .data(status)
                .build();

        return ResponseEntity.ok(response);
    }

    // 회원가입 처리
    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<Member>> memberInsertMethod(@ModelAttribute Member member,
                                                                  @RequestParam(name = "photofile", required = false) MultipartFile mfile) {
        log.info("Enrollment attempt: {}", member);

//		member.setUserPwd(bcryptPasswordEncoder.encode(member.getUserPwd()));
//		log.info("Encoded password: {}, length: {}", member.getUserPwd(), member.getUserPwd().length());

//		String savePath = "/path/to/save/photo_files"; // 실제 경로로 변경
//		Path saveDirectory = Paths.get(savePath);

        // 비밀번호 암호화
        if (StringUtils.hasText(member.getUserPwd())) {
            member.setUserPwd(memberService.encodedPassword(member.getUserPwd()));
            log.info("Encoded password: {}", member.getUserPwd());
        }

        // 파일 업로드 처리
        if (mfile != null && !mfile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mfile.getOriginalFilename()));

            // 파일 이름 검증(예: 허용된 확장자만)
            // 파일 이름 검증 (예: 허용된 확장자만)
            if (!isValidFileExtension(fileName)) {
                ApiResponse<Member> response = ApiResponse.<Member>builder()
                        .success(false)
                        .message("허용되지 않는 파일 형식입니다.")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String renameFileName = member.getUserId() + "_" + fileName;

            try {
                Path saveDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
                if (!Files.exists(saveDirectory)) {
                    Files.createDirectories(saveDirectory);
                }

                Path targetLocation = saveDirectory.resolve(renameFileName);
                Files.copy(mfile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                member.setPhotoFileName(renameFileName);
            } catch (IOException e) {
                log.error("File upload failed: {}", e.getMessage(), e);
                ApiResponse<Member> response = ApiResponse.<Member>builder()
                        .success(false)
                        .message("첨부파일 업로드 실패!")
                        .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }

        // 기본값 설정
        member.setLoginOk("Y");
        member.setAdminYN("N");
        member.setSignType("direct");



        // 역할 할당
        if ("Y".equalsIgnoreCase(member.getAdminYN())) {
            member.setRoles("ADMIN");
        } else {
            member.setRoles("USER");
        }
        log.info("Final member data: {}", member);

        int result = memberService.insertMember(member);
        if (result > 0) {
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(true)
                    .message("회원가입이 완료되었습니다.")
                    .data(member)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(false)
                    .message("회원 가입 실패! 확인하고 다시 가입해 주세요.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    private boolean isValidFileExtension(String fileName) {
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif"};
        for (String ext : allowedExtensions) {
            if (fileName.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    // '내 정보 보기' 처리
    /* userPwd(비밀번호 해시)는 절대 클라이언트에 노출되어서는 안 됨, 데이터 유출 시 큰 보안 사고임
    * 따라서 userPwd만 없는 새로운 MemberInfoDTO 클래스를 생성해서 반환함*/
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<MemberInfoDTO>> memberDetailMethod(@PathVariable("userId") String userId) {
        log.info("Fetching member info for userId: {}", userId);

        Optional<Member> memberOpt = memberService.selectMember(userId);

        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();

            // 원본 파일 이름 추출
            String originalFilename = null;
            if (member.getPhotoFileName() != null) {
                int underscoreIndex = member.getPhotoFileName().indexOf('_');
                originalFilename = underscoreIndex != -1 ? member.getPhotoFileName().substring(underscoreIndex + 1) : member.getPhotoFileName();
            }
            member.setPhotoFileName(originalFilename); // member DTO에 originalFileName 필드 추가 필요

            // MemberInfoDTO 로 변환
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
            ApiResponse<MemberInfoDTO> response = ApiResponse.<MemberInfoDTO>builder()
                    .success(true)
                    .message("회원 정보 조회 성공")
                    .data(memberInfoDTO)
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<MemberInfoDTO> response = ApiResponse.<MemberInfoDTO>builder()
                    .success(false)
                    .message(userId + "에 대한 회원 정보 조회 실패!")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 회원 정보 수정 처리
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<Member>> memberUpdateMethod(@PathVariable("userId") String userId,
                                                                  @ModelAttribute Member member,
                                                                  @RequestParam(name = "photofile", required = false) MultipartFile mfile) {
        log.info("Member update attempt: {}", member);

        // 기존 회원 정보 조회
        Optional<Member> existingMemberOpt = memberService.selectMember(userId);
        if (!existingMemberOpt.isPresent()) {
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(false)
                    .message("회원 정보가 존재하지 않습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Member existingMember = existingMemberOpt.get();

        // 비밀번호 변경 여부 확인
        if (StringUtils.hasText(member.getUserPwd())) {
            existingMember.setUserPwd(memberService.encodedPassword(member.getUserPwd()));
            log.info("Encoded new password: {}, length: {} for userId: {}", member.getUserPwd(), member.getUserPwd().length(), userId);
        }

//        String savePath = "/path/to/save/photo_files"; // 실제 경로로 변경
//        Path saveDirectory = Paths.get(savePath);

        // 파일 업로드 처리
        if (mfile != null && !mfile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mfile.getOriginalFilename()));

            // 파일 이름 검증(예: 허용된 확장자만)
            if (!isValidFileExtension(fileName)) {
                ApiResponse<Member> response = ApiResponse.<Member>builder()
                        .success(false)
                        .message("허용되지 않는 파일 형식입니다.")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (!fileName.equals(existingMember.getPhotoFileName())) {
                String renameFileName = member.getUserId() + "_" + fileName;

                try {
                    Path saveDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
                    if (!Files.exists(saveDirectory)) {
                        Files.createDirectories(saveDirectory);
                    }
                    Path targetLocation = saveDirectory.resolve(renameFileName);
                    Files.copy(mfile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                    existingMember.setPhotoFileName(renameFileName);
                } catch (IOException e) {
                    log.error("File upload failed: {}", e.getMessage(), e);
                    ApiResponse<Member> response = ApiResponse.<Member>builder()
                            .success(false)
                            .message("첨부파일 업로드 실패")
                            .build();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            }

        } else {
            existingMember.setPhotoFileName(userId + "_" + existingMember.getPhotoFileName());
        }

        existingMember.setLastModified(new Date(System.currentTimeMillis()));
        existingMember.setAdminYN(member.getAdminYN());
        existingMember.setLoginOk(member.getLoginOk());
        existingMember.setSignType(member.getSignType());

        int result = memberService.updateMember(existingMember);
        if (result > 0) {
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(true)
                    .message("회원 정보 수정 완료")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Member> response = ApiResponse.<Member>builder()
                    .success(false)
                    .message("회원 정보 수정 실패! 확인하고 다시 시도해주세요.")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 회원 삭제 처리
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> memberDeleteMethod(@PathVariable("userId") String userId) {
        log.info("Deleting member with userId: {}", userId);

        int result = memberService.deleteMember(userId);
        if (result > 0) {
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(true)
                    .message("회원 탈퇴가 완료되었습니다.")
                    .data("삭제된 userId: " + userId)
                    .build();
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(false)
                    .message(userId + "님의 회원 탈퇴 실패! 관리자에게 문의하세요. ")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Member>>> memberListMethod(
            @RequestParam(name = "page", defaultValue = "1") int currentPage,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "sort", defaultValue = "enrollDate,desc") String[] sort) {
        log.info("Fetching member list: page {}, limit {}, sort {}", currentPage, limit, sort);


        // 정렬 처리
        Sort.Direction direction = Sort.Direction.DESC;
        String sortBy = "enrollDate"; // 기본 정렬 필드

        if (sort.length == 2) {
            sortBy = sort[0];
            direction = sort[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(currentPage - 1, limit, Sort.by(direction, sortBy));
        Page<Member> pageResult = memberService.getAllMembers(pageable);

        ApiResponse<Page<Member>> response = ApiResponse.<Page<Member>>builder()
                .success(true)
                .message("회원 목록 조회 성공")
                .data(pageResult)
                .build();

        return ResponseEntity.ok(response);

    }

    // 로그인 제한/허용 처리
    @PutMapping("/{userId}/loginok")
    public ResponseEntity<ApiResponse<Member>> changeLoginOKMethod(@RequestBody Member member, @PathVariable String userId) {

        log.info("Updating login OK for userId: {}", userId);

        member.setUserId(userId); // URL에서 받은 userId를 설정

        int result = memberService.updateLoginOK(member);


        if (result > 0) {
            Optional<Member> updatedMemberOpt = memberService.selectMember(userId);
            if (updatedMemberOpt.isPresent()) {
                ApiResponse<Member> response = ApiResponse.<Member>builder()
                        .success(true)
                        .message("로그인 제한/허용 상태가 변경되었습니다.")
                        .data(updatedMemberOpt.get())
                        .build();
                return ResponseEntity.ok(response);
            }
        }

        ApiResponse<Member> response = ApiResponse.<Member>builder()
                .success(false)
                .message("로그인 제한/허용 처리에 실패했습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

    }

    // 관리자용 검색 기능
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Member>>> memberSearchMethod(@RequestParam("action") String action,
                                                                        @RequestParam(value = "keyword", required = false) String keyword,
                                                                        @RequestParam(value = "begin", required = false) String beginStr,
                                                                        @RequestParam(value = "end", required = false) String endStr,
                                                                        @RequestParam(value = "page", defaultValue = "1") int currentPage,
                                                                        @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                                        @RequestParam(name = "sort", defaultValue = "enrollDate,desc") String[] sort) {


        log.info("Member search: action={}, keyword={}, begin={}, end={}, page={}, limit={}, sort={}",
                action, keyword, beginStr, endStr, currentPage, limit, sort);


        // 정렬 처리
        Sort.Direction direction = Sort.Direction.DESC;
        String sortBy = "enrollDate"; // 기본 정렬 필드

        if (sort.length == 2) {
            sortBy = sort[0];
            direction = sort[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(currentPage - 1, limit, Sort.by(direction, sortBy));

        // 검색 결과 조회
        Page<Member> searchResult = memberService.searchMembers(action, keyword, beginStr, endStr, pageable);

        if(searchResult.hasContent()){
            ApiResponse<Page<Member>> response = ApiResponse.<Page<Member>>builder()
                    .success(true)
                    .message("검색 결과 조회 성공")
                    .data(searchResult)
                    .build();
            return ResponseEntity.ok(response);
        }else{
            ApiResponse<Page<Member>> response = ApiResponse.<Page<Member>>builder()
                    .success(false)
                    .message("검색 결과가 존재하지 않습니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }


    }
}
