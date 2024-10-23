package org.myweb.first.member.model.dao;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.myweb.first.member.model.dto.Member;
import org.myweb.first.member.model.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository("memberDao")
public class MemberDao {
	//쿼리문은 마이바티스 매퍼 파일에 쿼리문 별도 작성하
	//root-context.xml 에 설정된 마이바티스 객체를 연결 사용함
	@Autowired  //root-context.xml 에서 생성한 객체를 자동 연결함
	private SqlSessionTemplate sqlSessionTemplate;  
	
	//로그인 처리용
	public User selectLogin(User user) {
		//마이바티스가 제공하는 selectOne("매퍼의 앨리먼트 id명", 전달할 객체) : Object => 객체 하나 반환됨
		return sqlSessionTemplate.selectOne("memberMapper.selectLogin", user);		
	}
	
	//회원가입시 아이디 중복 검사용
	public int selectCheckId(String userid) {
		return sqlSessionTemplate.selectOne("memberMapper.selectCheckId", userid);
	}
	
	//회원가입 처리용
	public int insertMember(Member member) {
		return sqlSessionTemplate.insert("memberMapper.insertMember", member);
	}
	
	//내 정보 보기 요청 처리용
	public Member selectMember(String userId) {
		return sqlSessionTemplate.selectOne("memberMapper.selectMember", userId);
	}
	
	//회원 정보 수정 요청 처리용
	public int updateMember(Member member) {
		return sqlSessionTemplate.update("memberMapper.updateMember", member);
	}
	
	//회원 삭제 처리용
	public int deleteMember(String userId) {
		return sqlSessionTemplate.delete("memberMapper.deleteMember", userId);
	}

	//관리자용 ************************************************************
	public int selectListCount() {
		return sqlSessionTemplate.selectOne("memberMapper.selectListCount");
	}

	public ArrayList<Member> selectList(Paging paging) {
		List<Member> list = sqlSessionTemplate.selectList("memberMapper.selectList", paging);
		return (ArrayList<Member>)list;
	}

	//로그인 제한, 허용 처리용
	public int updateLoginOK(Member member) {
		return sqlSessionTemplate.update("memberMapper.updateLoginOK", member);
	}

	//검색 목록 카운트용
	public int selectSearchUserIdCount(String keyword) {
		return sqlSessionTemplate.selectOne("memberMapper.selectSearchUserIdCount", keyword);
	}

	public int selectSearchGenderCount(String keyword) {
		return sqlSessionTemplate.selectOne("memberMapper.selectSearchGenderCount", keyword);
	}

	public int selectSearchAgeCount(int age) {
		return sqlSessionTemplate.selectOne("memberMapper.selectSearchAgeCount", age);
	}

	public int selectSearchEnrollDateCount(Search search) {
		return sqlSessionTemplate.selectOne("memberMapper.selectSearchEnrollDateCount", search);
	}


	public int selectSearchLoginOKCount(String keyword) {
		return sqlSessionTemplate.selectOne("memberMapper.selectSearchLoginOKCount", keyword);
	}

	//검색 목록 조회용
	public ArrayList<Member> selectSearchUserId(Search search) {
		List<Member> list = sqlSessionTemplate.selectList("memberMapper.selectSearchUserId", search);
		return (ArrayList<Member>)list;
	}

	public ArrayList<Member> selectSearchGender(Search search) {
		List<Member> list = sqlSessionTemplate.selectList("memberMapper.selectSearchGender", search);
		return (ArrayList<Member>)list;
	}

	public ArrayList<Member> selectSearchAge(Search search) {
		List<Member> list = sqlSessionTemplate.selectList("memberMapper.selectSearchAge", search);
		return (ArrayList<Member>)list;
	}

	public ArrayList<Member> selectSearchEnrollDate(Search search) {
		List<Member> list = sqlSessionTemplate.selectList("memberMapper.selectSearchEnrollDate", search);
		return (ArrayList<Member>)list;
	}

	public ArrayList<Member> selectSearchLoginOK(Search search) {
		List<Member> list = sqlSessionTemplate.selectList("memberMapper.selectSearchLoginOK", search);
		return (ArrayList<Member>)list;
	}

}









