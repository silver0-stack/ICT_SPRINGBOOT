package org.myweb.first.member.model.service;

import java.util.ArrayList;

import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.myweb.first.member.model.dao.MemberDao;
import org.myweb.first.member.model.dto.Member;
import org.myweb.first.member.model.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//스프링에서는 서비스 인터페이스를 상속받은 후손클래스를 작성하도록 정해놓음
@Service("memberService")
public class MemberServiceImpl implements MemberService {
	//dao 와 연결 처리 : DI (Dependency Injection : 의존성 주입)
	@Autowired  //자동 객체 생성되면서 연결됨
	private MemberDao memberDao;
	
	@Override
	public User selectLogin(User user) {		
		return memberDao.selectLogin(user);	
	}

	@Override
	public int insertMember(Member member) {
		return memberDao.insertMember(member);
	}

	@Override
	public Member selectMember(String userId) {
		return memberDao.selectMember(userId);
	}

	@Override
	public int updateMember(Member member) {
		return memberDao.updateMember(member);
	}

	@Override
	public int deleteMember(String userId) {
		return memberDao.deleteMember(userId);
	}
	

	@Override
	public int selectCheckId(String userid) {
		return memberDao.selectCheckId(userid);
	}

	//관리자용 ******************************************
	@Override
	public int selectListCount() {
		return memberDao.selectListCount();
	}

	@Override
	public ArrayList<Member> selectList(Paging paging) {
		return memberDao.selectList(paging);
	}

	@Override
	public int updateLoginOK(Member member) {
		return memberDao.updateLoginOK(member);
	}

	//검색 카운트 관련 ------------------------------------------------------------------
	@Override
	public int selectSearchUserIdCount(String keyword) {
		return memberDao.selectSearchUserIdCount(keyword);
	}

	@Override
	public int selectSearchGenderCount(String keyword) {
		return memberDao.selectSearchGenderCount(keyword);
	}

	@Override
	public int selectSearchAgeCount(int age) {
		return memberDao.selectSearchAgeCount(age);
	}

	@Override
	public int selectSearchEnrollDateCount(Search search) {
		return memberDao.selectSearchEnrollDateCount(search);
	}

	@Override
	public int selectSearchLoginOKCount(String keyword) {
		return memberDao.selectSearchLoginOKCount(keyword);
	}

	//검색 관련 목록 조회용 --------------------------------------------------------------------
	@Override
	public ArrayList<Member> selectSearchUserId(Search search) {
		return memberDao.selectSearchUserId(search);
	}

	@Override
	public ArrayList<Member> selectSearchGender(Search search) {
		return memberDao.selectSearchGender(search);
	}

	@Override
	public ArrayList<Member> selectSearchAge(Search search) {
		return memberDao.selectSearchAge(search);
	}

	@Override
	public ArrayList<Member> selectSearchEnrollDate(Search search) {
		return memberDao.selectSearchEnrollDate(search);
	}

	@Override
	public ArrayList<Member> selectSearchLoginOK(Search search) {
		return memberDao.selectSearchLoginOK(search);
	}

}
