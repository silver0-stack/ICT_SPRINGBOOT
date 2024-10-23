package org.myweb.first.member.model.service;

import java.util.ArrayList;

import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.myweb.first.member.model.dto.Member;
import org.myweb.first.member.model.dto.User;

//스프링에서는 비즈니스 모델의 서비스 클래스는 인터페이스로 만들도록 정해져 있음
//인터페이스는 추상메소드만 멤버로 가지는 추상클래스이다.
//작성되는 추상메소드는 기본 public abstract 반환형 메소드명(자료형 매개변수, ....);
//public abstract 는 표기 생략함 => 상속받는 후손이 오버라이딩할 때 public 반드시 표기해야 함
public interface MemberService {
	User selectLogin(User user);	
	Member selectMember(String userId);
	int selectCheckId(String userid);
	//dml
	int insertMember(Member member);
	int updateMember(Member member);
	int deleteMember(String userId);	
	//관리자용
	int selectListCount();
	ArrayList<Member> selectList(Paging paging);
	int updateLoginOK(Member member);  //로그인 제한/허용 수정용
	//관리자용 회원관리 검색용
	int selectSearchUserIdCount(String keyword);
	int selectSearchGenderCount(String keyword);
	int selectSearchAgeCount(int age);
	int selectSearchEnrollDateCount(Search search);
	int selectSearchLoginOKCount(String keyword);
	ArrayList<Member> selectSearchUserId(Search search);
	ArrayList<Member> selectSearchGender(Search search);
	ArrayList<Member> selectSearchAge(Search search);
	ArrayList<Member> selectSearchEnrollDate(Search search);
	ArrayList<Member> selectSearchLoginOK(Search search);
}









