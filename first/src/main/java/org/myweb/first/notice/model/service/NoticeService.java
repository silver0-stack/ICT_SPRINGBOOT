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

@Slf4j	//Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;


    //JPA 제공 메소드로 해결하지 못하는 SQL 문 처리를 위한 별도의 리포지터리
	//상속, 재구현 없는 형식
	//private final NoticeQueryRepository noticeQueryRepository;

    // 페이징 처리된 공지사항 목록 조회
    public Page<NoticeEntity> getNoticeWithPagination(Pageable pageable){
        return noticeRepository.findAll(pageable);
    }

    // 특정 공지사항 존재 여부 확인
    public Optional<NoticeEntity> getNoticeById(String noticeId){
        return noticeRepository.findById(noticeId);
    }

    // 공지사항 추가
    public NoticeEntity createNotice(NoticeEntity noticeEntity){
        return noticeRepository.save(noticeEntity);
    }

    // 공지사항 업데이트
    public NoticeEntity updateNotice(String noticeId, NoticeEntity updatedNotice){
        Optional<NoticeEntity> optionalNotice = noticeRepository.findById(noticeId);
        if(optionalNotice.isPresent()){
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
            return noticeRepository.save(oldNotice);
        }
        return null;
    }


    // 공지사항 삭제
    public void deleteNotice(String noticeId){
        noticeRepository.deleteById(noticeId);
    }


	public ArrayList<Notice> selectTop3() {
		//jpa 가 제공하는 메소드 사용한다면
		//최근 공지글 3개 조회이므로, 공지번호 기준 내림차순정렬해서 상위 3개 추출함
		//findAll(Sort.by(Sort.Direction.DESC, "noticeNo")) : List<NoticeEntity>
		List<NoticeEntity> entityList = noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "noticeNo"));
		//select * from notice order by noticeno desc : 쿼리문 실행될 것임
		//3개만 추출함
		ArrayList<Notice> list = new ArrayList<>();
		for(int index = 0; index < 3; index++){
			list.add(entityList.get(index).toDto());
		}
		return list;
	}


	@Transactional
	public Notice updateAddReadCount(String noticeId) {
		Optional<NoticeEntity> entity = noticeRepository.findById(noticeId);
        if(entity.isPresent()){
            NoticeEntity noticeEntity = entity.get();
            log.info("addReadCount : {}" , noticeEntity);
            noticeEntity.setNotReadCount(noticeEntity.getNotReadCount() + 1);
            return noticeRepository.save(noticeEntity).toDto();	//jpa가 제공

        }
        return null;
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