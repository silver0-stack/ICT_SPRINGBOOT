package org.myweb.first.member.model.service;

import java.util.ArrayList;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.common.Search;
import org.myweb.first.member.jpa.entity.MemberEntity;
import org.myweb.first.member.jpa.repository.MemberRepository;
import org.myweb.first.member.model.dto.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor	//매개변수 있는 생성자를 반드시 실행시켜야 한다는 설정임
@Transactional
public class MemberService {
	@Autowired
	private final MemberRepository memberRepository;

	@Transactional
	public int insertMember(Member member) {
		//save() -> 성공시 Entity, 실패시 null 리턴함, JPA 가 제공하는 메소드임
		try{
			memberRepository.save(member.toEntity()).toDto();
			return 1;
		}catch (Exception e) {
			log.error(e.getMessage());
			return 0;
		}
	}

	public Member selectMember(String userId) {
		//jpa 제공 메소드 사용
		//findById(id) : Optional<T>
		//엔티티에 등록된 id 를 사용해서 entity 조회함
		Optional<MemberEntity> entityOptional = memberRepository.findById(userId);
		return entityOptional.get().toDto();
	}

	@Transactional
	public int updateMember(Member member) {
		//save() -> 성공시 Entity, 실패시 null 리턴함, JPA 가 제공하는 메소드임
		try{
			memberRepository.save(member.toEntity()).toDto();
			return 1;
		}catch (Exception e) {
			log.error(e.getMessage());
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
			log.info(e.getMessage());
			return 0;
		}
	}

	public int selectCheckId(String userid) {
		return 0;
	}

	//관리자용 ******************************************

	public int selectListCount() {
		return (int)memberRepository.count();
	}

	public ArrayList<Member> selectList(Pageable pageable) {
		Page<MemberEntity> entityList = memberRepository.findAll(pageable);
		ArrayList<Member> list = new ArrayList<>();
		//Page 안의 NoticeEntity 를 Notice 로 변환해서 리스트에 추가 처리함
		for(MemberEntity entity : entityList){
			list.add(entity.toDto());
		}
		return list;
	}

	public int updateLoginOK(Member member) {
		return 0;
	}

	//검색 카운트 관련 ------------------------------------------------------------------
	public int selectSearchUserIdCount(String keyword) {
		return 0;
	}


	public int selectSearchGenderCount(String keyword) {
		return 0;
	}


	public int selectSearchAgeCount(int age) {
		return 0;
	}


	public int selectSearchEnrollDateCount(Search search) {
		return 0;
	}


	public int selectSearchLoginOKCount(String keyword) {
		return 0;
	}

	//검색 관련 목록 조회용 --------------------------------------------------------------------

	public ArrayList<Member> selectSearchUserId(Search search) {
		return null;
	}


	public ArrayList<Member> selectSearchGender(Search search) {
		return null;
	}


	public ArrayList<Member> selectSearchAge(Search search) {
		return null;
	}


	public ArrayList<Member> selectSearchEnrollDate(Search search) {
		return null;
	}


	public ArrayList<Member> selectSearchLoginOK(Search search) {
		return null;
	}

}
