package org.myweb.first.member.model.service;

import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.myweb.first.member.model.dto.Member;
import org.myweb.first.member.model.dto.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

//스프링에서는 서비스 인터페이스를 상속받은 후손클래스를 작성하도록 정해놓음
@Service("memberService")
public class MemberService {
    //dao 와 연결 처리 : DI (Dependency Injection : 의존성 주입)
/*	@Autowired  //자동 객체 생성되면서 연결됨
	private MemberDao memberDao;*/

    public User selectLogin(User user) {
        return null;
    }

    public int insertMember(Member member) {
        return 0;
    }

    public Member selectMember(String userId) {
        return null;
    }

    public int updateMember(Member member) {
        return 0;
    }

    public int deleteMember(String userId) {
        return 0;
    }


    public int selectCheckId(String userid) {
        return 0;
    }

    //관리자용 ******************************************
    public int selectListCount() {
        return 0;
    }

    public ArrayList<Member> selectList(Paging paging) {
        return null;
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

