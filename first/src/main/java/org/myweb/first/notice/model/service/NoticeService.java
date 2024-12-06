package org.myweb.first.notice.model.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.board.jpa.entity.BoardEntity;
import org.myweb.first.common.Search;
import org.myweb.first.notice.jpa.entity.NoticeEntity;
//import org.myweb.first.notice.jpa.repository.NoticeQueryRepository;
import org.myweb.first.notice.jpa.repository.NoticeRepository;
import org.myweb.first.notice.model.dto.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    //JPA 제공 메소드로 해결하지 못하는 SQL 문 처리를 위한 별도의 리포지터리
    //상속, 재구현 없는 형식
    //private final NoticeQueryRepository noticeQueryRepository;


    public Notice createNotice(Notice newNotice){
        NoticeEntity savedEntity = noticeRepository.save(newNotice.toEntity());
        return savedEntity.toDto();
    }

    /**
     * 페이징 처리된 공지사항 목록 조회
     *
     * @param pageable
     * @return
     */
    public Page<Notice> getNoticeWithPagination(Pageable pageable) {
        Page<NoticeEntity> entityPage = noticeRepository.findAll(pageable);
        return entityPage.map(NoticeEntity::toDto);
    }


    /**
     * 공지사항 조회 (조회수 증가 포함)
     * @param noticeId
     * @return
     */
    @Transactional
    public Notice getNoticeById(String noticeId) {
        Optional<NoticeEntity> optionalNotice = noticeRepository.findById(noticeId);
        if (optionalNotice.isEmpty()) {
            throw new IllegalArgumentException("공지사항을 찾을 수 없습니다.");
        }
        NoticeEntity notice = optionalNotice.get();

        // 조회수 증가
        notice.setNotReadCount(notice.getNotReadCount() + 1);

        // 데이터베이스에 업데이트 반영
        noticeRepository.save(notice);

        return notice.toDto();
    }

    /**
     * 공지사항 업데이트
     * @param noticeId
     * @param updatedNotice
     * @return
     */
    // 공지사항 업데이트
    public Notice updateNotice(String noticeId, Notice updatedNotice) {
        Optional<NoticeEntity> optionalNotice = noticeRepository.findById(noticeId);
        if (optionalNotice.isEmpty()) {
            throw new IllegalArgumentException("공지사항을 찾을 수 없습니다.");
        }
        // 수정 전의 공지사항 엔터티
        NoticeEntity oldNotice = optionalNotice.get();
        if (updatedNotice.getNotTitle() != null) {
            oldNotice.setNotTitle(updatedNotice.getNotTitle());
        }
        if (updatedNotice.getNotContent() != null) {
            oldNotice.setNotContent(updatedNotice.getNotContent());
        }
        if (updatedNotice.getNotUpdatedBy() != null) {
            oldNotice.setNotUpdatedBy(updatedNotice.getNotUpdatedBy());
        }
        oldNotice.setNotUpdatedAt(new Timestamp(System.currentTimeMillis()));
        // 수정된 공지사항 엔터티를 DB에 저장
        NoticeEntity newNotice =  noticeRepository.save(oldNotice);
        return newNotice.toDto(); // DTO로 반환
    }


    // 공지사항 삭제
    public void deleteNotice(String noticeId) {
        noticeRepository.deleteById(noticeId);
    }


    /**
     * 최근 공지사항 3개 조회
     * @return
     */
    public List<Notice> getTop3Notices() {
      List<NoticeEntity> entityList = noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "notCreatedAt"));
      return entityList.stream()
              .limit(3)
              .map(NoticeEntity::toDto)
              .toList();
    }


    //로직을 단계별로 처리한 코드 ------------------------------------------
	/*public ArrayList<Notice> selectList(Pageable pageable) {
		//jpa 제공 메소드 사용
		//findAll() : Entity 반환됨 => select * from notice 쿼리 자동 실행됨
		//page 단위로 list 조회를 하고자 한다면, 스프링이 제공하는 Pageable 객체를 사용함
		//findAll(Pageable 변수) : Page<NoticeEntity> 반환됨 => 한 페이지의 리스트 정보가 들어있음
		Page<NoticeEntity> entityList = noticeRepository.findAll(pageable);
		//컨트롤러로 리턴할 ArrayList<Notice> 타입으로 변경 처리 필요함
		ArrayList<Notice> list = new ArrayList<>();
		//Page 안의 NoticeEntity 를 Notice 로 변환해서 리스트에 추가 처리함
		for(NoticeEntity entity : entityList){
			list.add(entity.toDto());
		}
		return list;
	}*/


    //검색용 메소드 --------------------------------------------------------
//	public ArrayList<Notice> selectSearchTitle(String keyword, Pageable pageable) {
//		return toList(noticeQueryRepository.findSearchTitle(keyword, pageable));
//	}
//
//	public int selectSearchTitleCount(String keyword) {
//		return (int)noticeQueryRepository.countSearchTitle(keyword);
//	}
//
//	public ArrayList<Notice> selectSearchContent(String keyword, Pageable pageable) {
//		return toList(noticeQueryRepository.findSearchContent(keyword, pageable));
//	}
//
//	public int selectSearchContentCount(String keyword) {
//		return (int)noticeQueryRepository.countSearchContent(keyword);
//	}
//
//	public ArrayList<Notice> selectSearchDate(Search search, Pageable pageable) {
//		return toList(noticeQueryRepository.findSearchDate(search.getBegin(), search.getEnd(), pageable));
//	}
//
//	public int selectSearchDateCount(Search search) {
//		return (int)noticeQueryRepository.countSearchDate(search.getBegin(), search.getEnd());
//	}
}