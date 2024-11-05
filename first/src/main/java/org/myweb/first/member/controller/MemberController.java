package org.myweb.first.member.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.common.ApiResponse;
import org.myweb.first.common.LoginResponse;
import org.myweb.first.common.Paging;
import org.myweb.first.member.model.dto.Member;
import org.myweb.first.member.model.dto.User;
import org.myweb.first.member.model.service.MemberService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
import java.util.List;
import java.util.Optional;

// ...

@Slf4j
@RestController
@RequestMapping("/members")
@CrossOrigin(origins = "*")
@SessionAttributes("loginUser")  // 세션에 로그인한 회원 정보를 저장
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final BCryptPasswordEncoder bcryptPasswordEncoder;

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
	public ResponseEntity<ApiResponse<LoginResponse>> loginMethod(@RequestBody User user) {
		log.info("Login attempt: {}", user);

		// Optinal<Member>를 반환하여 호출 측에서 명식적으로 처리하도록 유도하기로 했음
		// -> 컨트롤러에서 명시적으로 유도하겠음
		Optional<Member> loginUser  = memberService.selectMember(user.getUserId());

		if (loginUser.isPresent() && bcryptPasswordEncoder.matches(user.getUserPwd(), loginUser.get().getUserPwd())) {
			session.setAttribute("loginUser", loginUser);
			status.setComplete();
			return "common/main";
		} else {
			model.addAttribute("message", "로그인 실패! 아이디나 암호를 다시 확인하세요. 또는 로그인 제한된 회원입니다. 관리자에게 문의하세요.");
			return "common/error";
		}
	}

	// 로그아웃 처리
	@GetMapping("/logout")
	public String logoutMethod(Model model) {
		// 세션 무효화는 스프링 시큐리티 등에서 처리하는 것이 일반적입니다.
		// 여기서는 수동으로 처리하고 있습니다.
		return "redirect:/main";
	}

	// ID 중복 검사
	@PostMapping("/idchk")
	@ResponseBody
	public ResponseEntity<String> dupCheckIdMethod(@RequestParam("userid") String userId) {
		int userIdCount = memberService.selectCheckId(userId);
		String returnStr = userIdCount == 0 ? "ok" : "dup";
		return ResponseEntity.ok(returnStr);
	}

	// 회원가입 처리
	@PostMapping("/enroll")
	public String memberInsertMethod(Member member, Model model,
									 @RequestParam(name = "photofile", required = false) MultipartFile mfile,
									 RedirectAttributes redirectAttributes) {
		log.info("Enrollment attempt: {}", member);

//		member.setUserPwd(bcryptPasswordEncoder.encode(member.getUserPwd()));
//		log.info("Encoded password: {}, length: {}", member.getUserPwd(), member.getUserPwd().length());

		String savePath = "/path/to/save/photo_files"; // 실제 경로로 변경
		Path saveDirectory = Paths.get(savePath);

		if (!mfile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mfile.getOriginalFilename());
			String renameFileName = member.getUserId() + "_" + fileName;

			try {
				Path targetLocation = saveDirectory.resolve(renameFileName);
				Files.copy(mfile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
				member.setPhotoFileName(renameFileName);
			} catch (IOException e) {
				log.error("File upload failed: {}", e.getMessage(), e);
				model.addAttribute("message", "첨부파일 업로드 실패!");
				return "common/error";
			}
		}

		member.setLoginOk("Y");
		member.setAdminYN("N");
		member.setSignType("direct");
		log.info("Final member data: {}", member);

		if (memberService.insertMember(member) > 0) {
			redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
			return "redirect:/loginPage";
		} else {
			model.addAttribute("message", "회원 가입 실패! 확인하고 다시 가입해 주세요.");
			return "common/error";
		}
	}

	// '내 정보 보기' 처리
	@GetMapping("/myinfo")
	public String memberDetailMethod(@RequestParam("userId") String userId, Model model) {
		log.info("Fetching member info for userId: {}", userId);

		Optional<Member> member = memberService.selectMember(userId);

		if (member.isPresent()) {
			String originalFilename = null;
			if (member.get().getPhotoFileName() != null) {
				int underscoreIndex = member.get().getPhotoFileName().indexOf('_');
				originalFilename = underscoreIndex != -1 ? member.get().getPhotoFileName().substring(underscoreIndex + 1) : member.get().getPhotoFileName();
			}
			model.addAttribute("member", member);
			model.addAttribute("ofile", originalFilename);
			return "member/infoPage";
		} else {
			model.addAttribute("message", userId + "에 대한 회원 정보 조회 실패!");
			return "common/error";
		}
	}

	// 회원 정보 수정 처리
	@PutMapping("/mupdate")
	public String memberUpdateMethod(Member member, Model model,
									 @RequestParam("photofile") MultipartFile mfile,
									 @RequestParam("originalUserPwd") String originalUserPwd,
									 @RequestParam("ofile") String originalFileName) {
		log.info("Member update attempt: {}", member);

		if (StringUtils.hasText(member.getUserPwd())) {
			member.setUserPwd(bcryptPasswordEncoder.encode(member.getUserPwd()));
			log.info("Encoded new password: {}, length: {}", member.getUserPwd(), member.getUserPwd().length());
		} else {
			member.setUserPwd(originalUserPwd);
		}

		String savePath = "/path/to/save/photo_files"; // 실제 경로로 변경
		Path saveDirectory = Paths.get(savePath);

		if (!mfile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mfile.getOriginalFilename());

			if (!fileName.equals(originalFileName)) {
				String renameFileName = member.getUserId() + "_" + fileName;

				try {
					Path targetLocation = saveDirectory.resolve(renameFileName);
					Files.copy(mfile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
					member.setPhotoFileName(renameFileName);
				} catch (IOException e) {
					log.error("File upload failed: {}", e.getMessage(), e);
					model.addAttribute("message", "첨부파일 업로드 실패!");
					return "common/error";
				}
			}
		} else {
			member.setPhotoFileName(member.getUserId() + "_" + originalFileName);
		}

		member.setLastModified(new Date(System.currentTimeMillis()));

		if (memberService.updateMember(member) > 0) {
			return "redirect:/main";
		} else {
			model.addAttribute("message", "회원 정보 수정 실패! 확인하고 다시 수정해 주세요.");
			return "common/error";
		}
	}

	// 회원 삭제 처리
	@DeleteMapping("/mdelete")
	public String memberDeleteMethod(@RequestParam("userid") String userId, Model model) {
		if (memberService.deleteMember(userId) > 0) {
			return "redirect:/logout";
		} else {
			model.addAttribute("message", userId + "님의 회원 탈퇴 실패! 관리자에게 문의하세요.");
			return "common/error";
		}
	}

	// 관리자용 회원 목록 보기
	@GetMapping
	public String memberListMethod(Model model,
								   @RequestParam(name = "page", defaultValue = "1") int currentPage,
								   @RequestParam(name = "limit", defaultValue = "10") int limit) {
		log.info("Fetching member list: page {}, limit {}", currentPage, limit);

		int listCount = memberService.selectListCount();
		Paging paging = new Paging(listCount, limit, currentPage, "mlist.do");
		paging.calculate();

		Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1, paging.getLimit(),
				Sort.by(Sort.Direction.DESC, "enrollDate"));

		List<Member> list = memberService.selectList(pageable);

		if (!list.isEmpty()) {
			model.addAttribute("list", list);
			model.addAttribute("paging", paging);
			model.addAttribute("currentPage", currentPage);
			return "member/memberListView";
		} else {
			model.addAttribute("message", currentPage + " 페이지 회원 목록 조회 실패!");
			return "common/error";
		}
	}

	// 로그인 제한/허용 처리
	@PutMapping("/loginok")
	public String changeLoginOKMethod(Member member, RedirectAttributes redirectAttributes, Model model) {
		if (memberService.updateLoginOK(member) > 0) {
			redirectAttributes.addFlashAttribute("message", "로그인 제한/허용이 성공적으로 변경되었습니다.");
			return "redirect:/mlist";
		} else {
			model.addAttribute("message", "로그인 제한 / 허용 처리 오류 발생!");
			return "common/error";
		}
	}

	// 관리자용 검색 기능
	@GetMapping("/msearch")
	public String memberSearchMethod(@RequestParam("action") String action,
									 @RequestParam(value = "keyword", required = false) String keyword,
									 @RequestParam(value = "begin", required = false) String beginStr,
									 @RequestParam(value = "end", required = false) String endStr,
									 @RequestParam(value = "page", defaultValue = "1") int currentPage,
									 @RequestParam(value = "limit", defaultValue = "10") int limit,
									 Model model) {
		log.info("Member search: action={}, keyword={}, begin={}, end={}, page={}, limit={}",
				action, keyword, beginStr, endStr, currentPage, limit);

		int listCount = 0;
		Pageable pageable = PageRequest.of(currentPage - 1, limit, Sort.by(Sort.Direction.DESC, "enrollDate"));

		switch (action) {
			case "id":
				listCount = memberService.selectSearchUserIdCount(keyword);
				break;
			case "gender":
				listCount = memberService.selectSearchGenderCount(keyword);
				break;
			case "age":
				listCount = memberService.selectSearchAgeCount(Integer.parseInt(keyword));
				break;
			case "enrolldate":
				Date begin = Date.valueOf(beginStr);
				Date end = Date.valueOf(endStr);
				listCount = memberService.selectSearchEnrollDateCount(begin, end);
				break;
			case "loginok":
				listCount = memberService.selectSearchLoginOKCount(keyword);
				break;
			default:
				model.addAttribute("message", "유효하지 않은 검색 유형입니다.");
				return "common/error";
		}

		Paging paging = new Paging(listCount, limit, currentPage, "msearch.do");
		paging.calculate();

		List<Member> list = null;

		switch (action) {
			case "id":
				list = memberService.selectSearchUserId(keyword, pageable);
				break;
			case "gender":
				list = memberService.selectSearchGender(keyword, pageable);
				break;
			case "age":
				list = memberService.selectSearchAge(Integer.parseInt(keyword), pageable);
				break;
			case "enrolldate":
				Date beginDate = Date.valueOf(beginStr);
				Date endDate = Date.valueOf(endStr);
				list = memberService.selectSearchEnrollDate(beginDate, endDate, pageable);
				break;
			case "loginok":
				list = memberService.selectSearchLoginOK(keyword, pageable);
				break;
		}

		if (list != null && !list.isEmpty()) {
			model.addAttribute("list", list);
			model.addAttribute("paging", paging);
			model.addAttribute("currentPage", currentPage);
			model.addAttribute("limit", limit);
			model.addAttribute("action", action);

			if (keyword != null) {
				model.addAttribute("keyword", keyword);
			} else {
				model.addAttribute("begin", beginStr);
				model.addAttribute("end", endStr);
			}

			return "member/memberListView";
		} else {
			model.addAttribute("message", "회원 관리 검색 결과가 존재하지 않습니다.");
			return "common/error";
		}
	}
}
