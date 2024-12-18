package org.myweb.first.member.model.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicUpdate;
import org.myweb.first.member.jpa.entity.MemberEntity;
//import org.myweb.first.member.jpa.repository.MemberQueryRepositoryImpl;
import org.myweb.first.member.jpa.repository.MemberRepository;
import org.myweb.first.member.model.dto.Member;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor    //매개변수 있는 생성자를 반드시 실행시켜야 한다는 설정임
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 설정
@DynamicUpdate // 변경된 필드만 업데이트하도록 설정
public class MemberService {
    private final MemberRepository memberRepository; // 회원 리포지토리
    private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화 인코더
//    private final MemberQueryRepositoryImpl memberQueryRepository;

    /**
     * 회원 가입 처리 메소드
     *
     * @return 처리 결과 (1: 성공, 0: 실패)
     * @Param member 회원 정보 DTO
     */
    @Transactional
    public Member insertMember(Member member) {
        //save() -> 성공시 Entity, 실패시 null 리턴함, JPA 가 제공하는 메소드임
        try {
            // UUID로 PK생성 후 toString()
            // MemberEntity에서 @PrePersist로 대체
            // member.setMemUuid(UUID.randomUUID().toString());
            // 비밀번호 암호화 로직
            member.setMemPw(passwordEncoder.encode(member.getMemPw()));
            MemberEntity memberEntity = member.toEntity();

            // 저장 전 memUuid 값 확인
            log.info("MemberEntity before save: {}", memberEntity);

            // userId 확인
            log.info("Saving MemberEntity with userId: {}", memberEntity.getMemId());

            // memberRepository.save() 시 @PrePersist가 호출되어 memUuid 생성됨
            MemberEntity savedMember = memberRepository.save(memberEntity);

            // 저장 후 memUuid 값 확인
            log.info("MemberEntity after save: {}", savedMember);


            return savedMember.toDto();
        } catch (DataIntegrityViolationException e) {
            log.error("Insert Member Data Integrity Violation: {}", e.getMessage(), e);
            return null;
        } catch (IllegalArgumentException e) {
            log.error("Insert Member Illegal Argument: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("Insert Member Error: {}", e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 회원 ID로 회원 조회
     *
     * @param memId 회원 ID
     * @return 회원 정보 DTO의 Optional
     */
    public Optional<Member> selectMemberByMemId(String memId) {
        Optional<MemberEntity> memberEntityOpt = memberRepository.findByMemId(memId);
        if (memberEntityOpt.isPresent()) {
            Member member = memberEntityOpt.get().toDto(); //  엔터티를 dto로 변환
            log.debug("Loaded member: {}", member);
            return Optional.of(member);
        }
        return Optional.empty(); // 회원 정보 없음
    }


    /**
     * 회원 UUID로 회원 조회
     *
     * @param memUuid 회원 ID
     * @return 회원 정보 DTO의 Optional
     */
    public Optional<Member> selectMemberByUuid(String memUuid) {
        Optional<MemberEntity> memberEntityOpt = memberRepository.findByMemUuid(memUuid);
        if (memberEntityOpt.isPresent()) {
            Member member = memberEntityOpt.get().toDto(); //  엔터티를 dto로 변환
            log.debug("Loaded member: {}", member);
            return Optional.of(member);
        }
        return Optional.empty(); // 회원 정보 없음
    }


    @Transactional
    public int updateMember(Member member) {
        try {
            log.info("Updating member with memUuid: {}", member);


            // 기존 회원 엔터티 조회
            Optional<MemberEntity> existingMemberOpt = memberRepository.findByMemUuid(member.getMemUuid());
            if (existingMemberOpt.isEmpty()) {
                throw new IllegalArgumentException("수정할 회원이 존재하지 않습니다.");
            }

            MemberEntity existingMember = existingMemberOpt.get();
            log.info("수정 전 존재하는 멤버 엔터티: {}", existingMember);


            // 전달된 데이터로만 업데이트 (null이 아닌 필드만 처리)
            if (member.getMemName() != null) {
                existingMember.setMemName(member.getMemName());
            }
            if (member.getMemCellphone() != null) {
                existingMember.setMemCellphone(member.getMemCellphone());
            }
            if (member.getMemPhone() != null) {
                existingMember.setMemPhone(member.getMemPhone());
            }
            if (member.getMemEmail() != null) {
                existingMember.setMemEmail(member.getMemEmail());
            }
            if (member.getMemRnn() != null) {
                existingMember.setMemRnn(member.getMemRnn());
            }
            if (member.getMemPw() != null && !member.getMemPw().isEmpty()) {
                existingMember.setMemPw(passwordEncoder.encode(member.getMemPw()));
            }
            if(member.getMemAddress() !=null && !member.getMemAddress().isEmpty()) {
                existingMember.setMemAddress(member.getMemAddress());
            }


            // 변경 사항 저장(JPA가 자동으로 업데이트 처리)
            memberRepository.save(existingMember);

            /*
             * [JPA의 영속성 컨텍스트 문제]
             * flush() 메소드를 호출하여 영속성 컨텍스트와 데이터베이스 상태를 강제로 동기화
             * */
            memberRepository.flush(); // 강제로 업데이트된 내용을 즉시 반영

            log.info("Member saved:{}", existingMember);

            // 성공
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Update Member Error: {}", e.getMessage());
            // 실패
            return 0;
        }
    }

    /**
     * 회원 삭제 메소드
     *
     * @param memUuid 회원 UUID
     * @return 처리 결과 (1: 성공, 0: 실패)
     */
    @Transactional
    public int deleteMember(String memUuid) {
        try {   //리턴 타입을 int 로 맞추기 위해서 처리함
            if (memberRepository.existsById(memUuid)) {
                memberRepository.deleteById(memUuid);
                log.info("Deleted member with memUuid: {}", memUuid);
                return 1;
            } else {
                log.warn("Attempted to delete non-existing member with memUuid: {}", memUuid);
                return 0;
            }
        } catch (Exception e) {
            log.error("Delete Member Error: {}", e.getMessage(), e);
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 회원 ID 중복 체크
     *
     * @param memId 회원 ID
     * @return 존재 여부 (1: 존재, 0: 존재하지 않음)
     */
    public int selectCheckId(String memId) {
        // existsById()는 엔터티의 기본 키 (PK) 기준으로 존재 여부를 검사한다.
        // 즉, memUuid로 조회하는 JPA 메소드인 것
        // 근데, 난 memId(사용자 아이디)를 기준으로 조회하고 싶으니 내가 repository에 커스텀 쿼리 메소드를 추가해야 한다
        //return memberRepository.existsById(memId) ? 1 : 0;    //jpa가 제공하는 existsById 메소드 사용
        Optional<MemberEntity> member = memberRepository.findByMemId(memId);
        return member.isPresent()? 1 : 0;    // Optional.isPresent()로 존재 여부를 확인
    }


    /**
     * 전체 회원 조회 메소드
     *
     * @param pageable 페이징 및 정렬 정보
     * @return 페이징된 회원 정보
     */
    public Page<Member> getAllMembers(Pageable pageable) {
        /*findAll(Pageable pageable)은 Spring Data JPA에서 제공하는 메소드로 페이징된 모든 데이터를 조회*/
        return memberRepository.findAll(pageable)
                .map(MemberEntity::toDto); // 엔터티를 DTO로 변환하여 반환
    }

    /*
     * 조건별 회원 검색 메소드
     * @param action 검색 기준 (id, gender, age, enrollDate, loginok)
     * @param keyword 검색 키워드
     * @param beginStr 검색 시작 날짜 (enrollDate 기준)
     * @param endStr 검색 종료 날짜 (enrollDate 기준)
     * @param pageable 페이징 및 정렬 정보
     * @return 페이징된 검색 결과
     * */
    // 조건별 회원 검색
//    public Page<Member> searchMembers(String action, String keyword, String beginStr, String endStr, Pageable pageable ){
//        Date begin = null;
//        Date end = null;
//
//        // enrollDate 기준 검색일 경우 날짜 변환
//        /*action이 enrollDaate이고 beginStr과 endStr이 존재하면 String을 Date로 변환*/
//        if("enrollDate".equals(action)&& StringUtils.hasText(beginStr)&&
//        StringUtils.hasText(endStr)){
//            begin=Date.valueOf(beginStr);
//            end=Date.valueOf(endStr);
//        }
//
//        // 쿼리 리포지토리를 통해 검색
//        return memberQueryRepository.searchMembers(action, keyword, begin, end, pageable)
//                .map(MemberEntity::toDto);
//    }
    /*
     * 회원 목록 조회 메소드 (페이징)
     * @param pageable 페이징 및 정렬 정보
     * @return 회원 목록
     */



    /*
     * 로그인 제한/허용 상태 수정 메소드
     * @param member 수정할 회원 정보 DTO
     * @return 처리 결과 (1: 성공, 0: 실패)
     * */
//    @Transactional
//    public int updateLoginOK(Member member) {
//        try {
//            // 기존 회원 엔터티 조회
//            MemberEntity existingEntity = memberRepository.findById(member.getUserId())
//                    .orElseThrow(() -> new IllegalArgumentException("Member not found with userId: " + member.getUserId()));
//            // 로그인 제한/허용 상태 수정
//            existingEntity.setLoginOk(member.getLoginOk());
//            // 수정된 엔터티 저장
//            memberRepository.save(existingEntity);
//            // 성공
//            return 1;
//        } catch (Exception e) {
//            log.error("Update LoginOK Error: {}", e.getMessage());
//            e.printStackTrace();
//            return 0; //실패
//        }
//    }


    /*
     * 비밀번호 매칭 메소드
     * @param rawPassword 입력된 비밀번호
     * @param encodedPassword 저장된 암호화된 비밀번호
     * @return 비밀번호 일치 여부
     * */
    // 비밀번호 매칭
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);

    }

    /*
     * 비밀번호 암호화 메소드
     * @param rawPasswowrd 입력된 비밀번호
     * @return 암호화된 비밀번호
     * */
    // 회원 정보 수정
    public String encodedPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }


//    public List<Member> selectList(Pageable pageable) {
//        return memberRepository
//                .findAll(pageable)
//                .stream()
//                .map(MemberEntity::toDto)
//                .collect(Collectors.toList());
//    }

//    //검색 카운트 관련 ------------------------------------------------------------------
//    public int selectSearchUserIdCount(String keyword) {
//        // 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
//        return (int) memberQueryRepository.countSearchUserId(keyword);
//    }
//
//
//    public int selectSearchGenderCount(String keyword) {
//        // 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
//        return (int) memberQueryRepository.countSearchGender(keyword);
//    }
//
//
//    public int selectSearchAgeCount(int age) {
//        // 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
//        return (int) memberQueryRepository.countSearchAge(age);
//    }
//
//
//    public int selectSearchEnrollDateCount(Date begin, Date end) {
//        // 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
//        return (int) memberQueryRepository.countSearchDate(begin, end);
//    }
//
//
//    public int selectSearchLoginOKCount(String keyword) {
//        // 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
//        return (int) memberQueryRepository.countSearchLoginOK(keyword);
//    }

    //검색 관련 목록 조회용 --------------------------------------------------------------------
//
//    public List<Member> selectSearchUserId(String keyword, Pageable pageable) {
//        return memberQueryRepository.findSearchUserId(keyword, pageable)
//                .stream()
//                .map(MemberEntity::toDto)
//                .collect(Collectors.toList());
//    }
//
//
//    public List<Member> selectSearchGender(String keyword, Pageable pageable) {
//        return memberQueryRepository.findSearchGender(keyword, pageable)
//                .stream()
//                .map(MemberEntity::toDto)
//                .collect(Collectors.toList());
//    }
//
//
//        public List<Member> selectSearchAge(int age, Pageable pageable) {
//        return memberQueryRepository.findSearchAge(age, pageable)
//                .stream()
//                .map(MemberEntity::toDto)
//                .collect(Collectors.toList());
//    }
//
//
//    public List<Member> selectSearchEnrollDate(Date begin, Date end, Pageable pageable) {
//        return memberQueryRepository.findSearchDate(begin, end, pageable)
//                .stream()
//                .map(MemberEntity::toDto)
//                .collect(Collectors.toList());
//    }
//
//
//    public List<Member> selectSearchLoginOK(String keyword, Pageable pageable) {
//        return memberQueryRepository.findSearchLoginOK(keyword, pageable)
//                .stream()
//                .map(MemberEntity::toDto)
//                .collect(Collectors.toList());
//    }
}
