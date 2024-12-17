package org.myweb.first.files.notice.jpa.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.files.notice.model.dto.NoticeFiles;

import java.util.UUID;

/**
 * NoticeFilesEntity 클래스는 NOTICE_FILES 테이블과 매핑되는 JPA 엔터티 클래스입니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "NOTICE_FILES")
public class NoticeFilesEntity {

    @Id
    @Column(name = "NF_ID", length = 100, nullable = false)
    private String nfId; // 첨부파일 고유 ID (Primary Key)

    @Column(name = "NF_NOT_ID", length = 100, nullable = false)
    private String nfNotId; // 공지사항 고유 ID (Foreign Key)

    @Column(name = "NF_ORIGINAL_NAME", length = 1000, nullable = false)
    private String nfOriginalName; // 원본 파일 이름

    @Column(name = "NF_RENAME", length = 1000, nullable = false)
    private String nfRename; // 저장용 파일 이름

    @PrePersist
    public void prePersist() {
        if (this.nfId == null) {
            this.nfId = UUID.randomUUID().toString();
        }
    }

    public NoticeFiles toDto() {
        return NoticeFiles.builder()
                .nfId(nfId)
                .nfNotId(nfNotId)
                .nfOriginalName(nfOriginalName)
                .nfRename(nfRename)
                .build();
    }
}