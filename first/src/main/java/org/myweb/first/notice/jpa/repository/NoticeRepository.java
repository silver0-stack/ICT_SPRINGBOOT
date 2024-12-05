package org.myweb.first.notice.jpa.repository;

import org.myweb.first.board.jpa.entity.BoardEntity;
import org.myweb.first.notice.jpa.entity.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

//JPA 는 Entity 와 Repository 를 만들어서 사용하는 기술임
//JPA의 Repository 는 JpaRepository 인터페이스를 상속받아서 후손 인터페이스로 만듦
//제네릭스는 <엔티티클래스명, @id 프로퍼티의 클래스자료형> 표기함
//MyBatis 의 SqlSession 과 같은 역할을 수행함. Mapper 인터페이스와 같음

@Repository
public interface NoticeRepository extends JpaRepository<NoticeEntity, String> {
    //jpa 가 제공하는 기본 메소드를 사용할 수 있게 됨

}
