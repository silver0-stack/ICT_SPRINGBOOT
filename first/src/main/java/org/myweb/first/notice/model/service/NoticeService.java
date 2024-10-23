package org.myweb.first.notice.model.service;

import java.util.ArrayList;

import org.myweb.first.common.Paging;
import org.myweb.first.common.Search;
import org.myweb.first.notice.model.dto.Notice;

public interface NoticeService {
	Notice selectLast();
	ArrayList<Notice> selectTop3();
	Notice selectNotice(int noticeNo);
	ArrayList<Notice> selectList(Paging paging);
	int selectListCount();
	//ajax test
	ArrayList<Notice> selectSearchTitle(String keyword);
	//dml method
	int insertNotice(Notice notice);
	int updateAddReadCount(int noticeNo);
	int deleteNotice(int noticeNo);
	int updateNotice(Notice notice);
	//검색용 메소드
	ArrayList<Notice> selectSearchTitle(Search search);
	int selectSearchTitleCount(String keyword);
	ArrayList<Notice> selectSearchContent(Search search);
	int selectSearchContentCount(String keyword);
	ArrayList<Notice> selectSearchDate(Search search);
	int selectSearchDateCount(Search search);
}
