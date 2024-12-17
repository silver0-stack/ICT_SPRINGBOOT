package org.myweb.first.files.notice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.files.notice.jpa.entity.NoticeFilesEntity;

import java.util.UUID;

/**
 * NoticeFiles 클래스는 NOTICE_FILES 테이블의 데이터를 전송하기 위한 DTO 클래스입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeFiles {
    private String nfId; // 첨부파일 고유 ID
    private String nfNotId; // 공지사항 고유 ID
    private String nfOriginalName; // 원본 파일 이름
    private String nfRename; // 저장용 파일 이름

    /**
     * DTO를 Entity로 변환하는 메소드
     */
    public NoticeFilesEntity toEntity(String noticeId) {
        return NoticeFilesEntity.builder()
                .nfId(this.nfId != null ? this.nfId : UUID.randomUUID().toString())
                .nfNotId(noticeId)
                .nfOriginalName(this.nfOriginalName)
                .nfRename(this.nfRename)
                .build();
    }
}
