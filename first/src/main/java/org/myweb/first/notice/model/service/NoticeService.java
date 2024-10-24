package org.myweb.first.notice.model.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.common.Search;
import org.myweb.first.notice.jpa.entity.NoticeEntity;
import org.myweb.first.notice.jpa.repository.NoticeRepository;
import org.myweb.first.notice.model.dto.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Slf4j // Log 객체 선언임. 별도의 로그객체 선언 필요없음. 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {
    // JPA 가 제공하는 기본 메소드 사용을 하려면
    private final NoticeRepository noticeRepository;


    // ArrayList<Notice> 리턴하는 메소드들이 사용하는 중복 코드는 별도의 메소드로 작성함
    private ArrayList<Notice> toList(Page<NoticeEntity> entityList) {
        // 컨트롤러로 리턴한 ArrayList<Notice) 타입으로 변경 처리 필요함
        ArrayList<Notice> list = new ArrayList<>();
        //Page 안의 Entity 를Notice 변환해서 리스트에 추가 처리함
        for(NoticeEntity entity : entityList) {
            list.add(entity.toDto());
        }
        return list;
    }

    public Notice selectLast() {
        return null;
    }

    public ArrayList<Notice> selectSearchTitle(String keyword) {
        return null;
    }

    @Transactional
    public int insertNotice(Notice notice) {
        // save(Entity) : Entity 메소드 사용
        // JPA 가 제공, insert 문, update 문 처리
        try {
            noticeRepository.save(notice.toEntity());
            return 1;
        }catch(Exception e) {
            log.info(e.getMessage());
//            e.printStackTrace();
            return 0;
        }
    }

    public ArrayList<Notice> selectTop3() {
        //jpa가 제공하는 메소드를 사용한다면
        //최근 공지글 3개 조회이므로,공지번호 기준 내림차순 정렬해서 상위 3개 추출함
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


    public Notice selectNotice(int noticeNo) {
        return null;
    }

    public int updateAddReadCount(int noticeNo) {
        return 0;
    }

/*	public ArrayList<Notice> selectList(Pageable pageable) {
		// JPA 제공 메소드 사용
		// findAll() : Entity 반환됨
		// page 단위로 list 조회를 하고자 한다면, 스프링이 제공하는 Pageable 객체를 사용함
		// findAll(Pageable 변수) : Page<NoticeEntity> 반환됨 => 한 페이지의 리스트 정보가 들어있음
		Page<NoticeEntity> entityList = noticeRepository.findAll(pageable);
		// 컨트롤러로 리턴한 ArrayList<Notice) 타입으로 변경 처리 필요함
		ArrayList<Notice> list = new ArrayList<>();
		//Page 안의 Entity 를Notice 변환해서 리스트에 추가 처리함
		for(NoticeEntity entity : entityList) {
			list.add(entity.toDto());
		}
		return null;
	}*/

    public ArrayList<Notice> selectList(Pageable pageable) {
		return toList(noticeRepository.findAll(pageable));
    }

    public int selectListCount() {
        return 0;
    }

    public int deleteNotice(int noticeNo) {
        try{
            // JPA 에서 deleteById() : Entity id 로 delete 처리
            noticeRepository.deleteById(noticeNo);
            return 1;
        } catch (Exception e) {
            log.info(e.getMessage());
            return 0;
        }
    }

    public int updateNotice(Notice notice) {
        // save(Entity) : Entity 메소드 사용
        // JPA 가 제공, insert 문, update 문 처리
        try {
            noticeRepository.save(notice.toEntity());
            return 1;
        }catch(Exception e) {
            log.info(e.getMessage());
//            e.printStackTrace();
            return 0;
        }
    }

    //검색용 메소드 --------------------------------------------------------

    public ArrayList<Notice> selectSearchTitle(Search search) {
        return null;
    }

    public int selectSearchTitleCount(String keyword) {
        return 0;
    }

    public ArrayList<Notice> selectSearchContent(Search search) {
        return null;
    }

    public int selectSearchContentCount(String keyword) {
        return 0;
    }

    public ArrayList<Notice> selectSearchDate(Search search) {
        return null;
    }

    public int selectSearchDateCount(Search search) {
        return 0;
    }

}