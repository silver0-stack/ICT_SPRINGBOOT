package org.myweb.first.notice.model.dao;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.myweb.first.notice.model.dto.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("noticeDao")
public class NoticeDao {
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;
	
	//ajax 테스트용 : 마지막 등록된 최근 공지글 한 개 조회용 메소드
	public Notice selectLast() {
		return sqlSessionTemplate.selectOne("noticeMapper.selectLast");
	}
	
	//ajax : 최근 등록 공지글 3개 조회용
	public ArrayList<Notice> selectTop3(){
		List<Notice> list = sqlSessionTemplate.selectList("noticeMapper.selectTop3");
		return (ArrayList<Notice>)list;
	}
	
	//상세보기 처리를 위한 공지글 1개 조회용
	public Notice selectNotice(int noticeNo) {
		return sqlSessionTemplate.selectOne("noticeMapper.selectNotice", noticeNo);
	}
	
	//공지 제목 검색
	public ArrayList<Notice> selectSearchTitle(String keyword){
		List<Notice> list = sqlSessionTemplate.selectList("noticeMapper.selectSearchTitleKeyword", keyword);
		return (ArrayList<Notice>)list;
	}
	
	//새 공지글 등록
	public int insertNotice(Notice notice) {
		return sqlSessionTemplate.insert("noticeMapper.insertNotice", notice);
	}

	//조회수 1증가 처리
	public int updateAddReadCount(int noticeNo) {
		return sqlSessionTemplate.update("noticeMapper.updateAddReadCount", noticeNo);
	}
	
	//공지글 삭제
	public int deleteNotice(int noticeNo) {
		return sqlSessionTemplate.delete("noticeMapper.deleteNotice", noticeNo);
	}
	
	//공지글 수정
	public int updateNotice(Notice notice) {
		return sqlSessionTemplate.update("noticeMapper.updateNotice", notice);
	}

	public ArrayList<Notice> selectList(Paging paging) {
		List<Notice> list = sqlSessionTemplate.selectList("noticeMapper.selectList", paging);
		return (ArrayList<Notice>)list;
	}
	
	public int selectListCount() {
		return sqlSessionTemplate.selectOne("noticeMapper.selectListCount");
	}

	//검색 관련 메소드 ------------------------------------------------------------------------------------
	public ArrayList<Notice> selectSearchTitle(Search search) {
		List<Notice> list = sqlSessionTemplate.selectList("noticeMapper.selectSearchTitle", search);
		return (ArrayList<Notice>)list;
	}

	public int selectSearchTitleCount(String keyword) {
		return sqlSessionTemplate.selectOne("noticeMapper.selectSearchTitleCount", keyword);
	}

	public ArrayList<Notice> selectSearchContent(Search search) {
		List<Notice> list = sqlSessionTemplate.selectList("noticeMapper.selectSearchContent", search);
		return (ArrayList<Notice>)list;
	}

	public int selectSearchContentCount(String keyword) {
		return sqlSessionTemplate.selectOne("noticeMapper.selectSearchContentCount", keyword);
	}

	public ArrayList<Notice> selectSearchDate(Search search) {
		List<Notice> list = sqlSessionTemplate.selectList("noticeMapper.selectSearchDate", search);
		return (ArrayList<Notice>)list;
	}

	public int selectSearchDateCount(Search search) {
		return sqlSessionTemplate.selectOne("noticeMapper.selectSearchDateCount", search);
	}
}






