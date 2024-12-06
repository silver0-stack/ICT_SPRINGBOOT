package org.myweb.first.notice.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.notice.model.dto.Notice;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.UUID;

//테이블 생성에 대한 가이드 클래스임
//@Entity 어노테이션 반드시 표시함 => 설정정보에 자동 등록됨 => Repository 와 연결됨

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "NOTICE") //매핑할 테이블 이름 지정함, NOTICE 테이블을 자동으로 만들어 주기도 하는 어노테이션임
@Entity     // JPA 가 관리함, DB 테이블과 DTO 클래스 매핑을 위해서 필요함
public class NoticeEntity {
    @Id
    @Column(name = "NOT_ID", length = 100, nullable = false)
    private String notId;

    @Column(name = "NOT_TITLE", length = 250, nullable = false)
    private String notTitle;

    @Lob
    @Column(name = "NOT_CONTENT", nullable = false)
    private String notContent;

    @Column(name = "NOT_CREATED_AT", nullable = false)
    private Timestamp notCreatedAt;

    @Column(name = "NOT_CREATED_BY")
    private String notCreatedBy;

    @Column(name = "NOT_UPDATED_AT", nullable = false)
    private Timestamp notUpdatedAt;

    @Column(name = "NOT_UPDATED_BY")
    private String notUpdatedBy;

    @Column(name = "NOT_DELETED_AT")
    private Timestamp notDeletedAt;

    @Column(name = "NOT_DELETED_BY")
    private String notDeletedBy;

    @Column(name = "NOT_READ_COUNT", nullable = false)
    private int notReadCount;

    //jpa 로 넘어가기 전(sql 에 적용하기 전)에 작동된다는 어노테이션임
    @PrePersist
    public void prePersist(){
        // @PrePersist를 활용해 persist() 호출 전에 notId가 없으면 UUID를 생성해 설정
        if(notId == null || notId.isEmpty()){
            notId = UUID.randomUUID().toString(); // UUID를 기본 키로 자동 생성
        }
        notCreatedAt = new Timestamp(System.currentTimeMillis());
        notUpdatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Notice toDto() {
        return Notice.builder()
                .notId(notId)
                .notTitle(notTitle)
                .notContent(notContent)
                .notCreatedAt(notCreatedAt)
                .notCreatedBy(notCreatedBy)
                .notUpdatedAt(notUpdatedAt)
                .notUpdatedBy(notUpdatedBy)
                .notDeletedAt(notDeletedAt)
                .notDeletedBy(notDeletedBy)
                .notReadCount(notReadCount)
                .build();
    }
}
