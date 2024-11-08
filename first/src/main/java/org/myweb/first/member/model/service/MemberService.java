package org.myweb.first.member.model.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.common.Search;
import org.myweb.first.member.jpa.entity.MemberEntity;
import org.myweb.first.member.jpa.repository.MemberQueryRepository;
import org.myweb.first.member.jpa.repository.MemberRepository;
import org.myweb.first.member.model.dto.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor    //매개변수 있는 생성자를 반드시 실행시켜야 한다는 설정임
@Transactional(readOnly=true) // 기본적으로 읽기 전용
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final BCryptPasswordEncoder passwordEncoder;


//	private ArrayList<Member> toList(List<MemberEntity> entityList) {
//		ArrayList<Member> list = new ArrayList<>();
//		for (MemberEntity entity : entityList) {
//			list.add(entity.toDto());
//		}
//		return list;
//	}

    @Transactional
    public int insertMember(Member member) {
        //save() -> 성공시 Entity, 실패시 null 리턴함, JPA 가 제공하는 메소드임
        try {
            memberRepository.save(member.toEntity()).toDto();
            return 1;
        } catch (DataIntegrityViolationException e) {
            log.error("Insert Member Data Integrity Violation: {}", e.getMessage(), e);
            return 0;
        } catch (Exception e) {
            log.error("Insert Member Error: {}", e.getMessage(), e);
            e.printStackTrace();
            return 0;
        }
    }

    public Optional<Member> selectMember(String userId) {
        Optional<MemberEntity> memberEntityOpt = memberRepository.findById(userId);
        if (memberEntityOpt.isPresent()) {
            Member member = memberEntityOpt.get().toDto();
            log.debug("Loaded member: {}", member);
            return Optional.of(member);
        }
        return Optional.empty();
    }


    @Transactional
    public int updateMember(Member member) {
        try {
            memberRepository.save(member.toEntity()).toDto();
            return 1;
        } catch (Exception e) {
            log.error("Update Member Error: {}", e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    @Transactional
    public int deleteMember(String userId) {
        try {   //리턴 타입을 int 로 맞추기 위해서 처리함
            //deleteById() -> 리턴 타입이 void 임
            //전달인자인 userid 가 null 인 경우 IllegalArgumentException 발생함
            memberRepository.deleteById(userId);
            return 1;
        } catch (Exception e) {
            log.error("Delete Member Error: {}", e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    // ID 중복 체크
    public int selectCheckId(String userid) {
        return memberRepository.existsById(userid) ? 1 : 0;    //jpa가 제공
    }

    /*관리자용 *************/
    public int selectListCount() {
        return (int) memberRepository.count();
    }

    // 전체 회원 조회
    public Page<Member> getAllMembers(Pageable pageable){
    return memberRepository.findAll(pageable)
            .map(MemberEntity::toDto);
    }

    // 조건별 회원 검색
    public Page<Member> searchMembers(String action, String keyword, String beginStr, String endStr, Pageable pageable ){
        Date begin = null;
        Date end = null;

        if("enrollDate".equals(action)&& StringUtils.hasText(beginStr)&&
        StringUtils.hasText(endStr)){
            begin=Date.valueOf(beginStr);
            end=Date.valueOf(endStr);
        }

        return memberQueryRepository.searchMembers(action, keyword, begin, end, pageable)
                .map(MemberEntity::toDto);
    }
    public List<Member> selectList(Pageable pageable) {
        return memberRepository
                .findAll(pageable)
                .stream()
                .map(MemberEntity::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public int updateLoginOK(Member member) {
        try {
            MemberEntity existingEntity = memberRepository.findById(member.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found with userId: " + member.getUserId()));
            existingEntity.setLoginOk(member.getLoginOk());
            memberRepository.save(existingEntity);
            return 1;
        } catch (Exception e) {
            log.error("Update LoginOK Error: {}", e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    //검색 카운트 관련 ------------------------------------------------------------------
    public int selectSearchUserIdCount(String keyword) {
        // 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
        return (int) memberQueryRepository.countSearchUserId(keyword);
    }


    public int selectSearchGenderCount(String keyword) {
        // 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
        return (int) memberQueryRepository.countSearchGender(keyword);
    }


    public int selectSearchAgeCount(int age) {
        // 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
        return (int) memberQueryRepository.countSearchAge(age);
    }


    public int selectSearchEnrollDateCount(Date begin, Date end) {
        // 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
        return (int) memberQueryRepository.countSearchDate(begin, end);
    }


    public int selectSearchLoginOKCount(String keyword) {
        // 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
        return (int) memberQueryRepository.countSearchLoginOK(keyword);
    }

    //검색 관련 목록 조회용 --------------------------------------------------------------------

    public List<Member> selectSearchUserId(String keyword, Pageable pageable) {
        return memberQueryRepository.findSearchUserId(keyword, pageable)
                .stream()
                .map(MemberEntity::toDto)
                .collect(Collectors.toList());
    }


    public List<Member> selectSearchGender(String keyword, Pageable pageable) {
        return memberQueryRepository.findSearchGender(keyword, pageable)
                .stream()
                .map(MemberEntity::toDto)
                .collect(Collectors.toList());
    }


        public List<Member> selectSearchAge(int age, Pageable pageable) {
        return memberQueryRepository.findSearchAge(age, pageable)
                .stream()
                .map(MemberEntity::toDto)
                .collect(Collectors.toList());
    }


    public List<Member> selectSearchEnrollDate(Date begin, Date end, Pageable pageable) {
        return memberQueryRepository.findSearchDate(begin, end, pageable)
                .stream()
                .map(MemberEntity::toDto)
                .collect(Collectors.toList());
    }


    public List<Member> selectSearchLoginOK(String keyword, Pageable pageable) {
        return memberQueryRepository.findSearchLoginOK(keyword, pageable)
                .stream()
                .map(MemberEntity::toDto)
                .collect(Collectors.toList());
    }


    // 비밀번호 매칭
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // 회원 정보 수정
    public String encodedPassword(String rawPassword){
        return passwordEncoder.encode(rawPassword);
    }

}
