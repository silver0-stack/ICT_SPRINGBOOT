package org.myweb.first.notice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.myweb.first.notice.jpa.entity.NoticeEntity;

import java.sql.Timestamp;
import java.util.Date;

@Data  //@Getter, @Setter, @ToString, @Equals, @HashCode 오버라이딩 까지 자동 생성됨
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notice {
	@NotBlank
	private String notId;
	private String notTitle;
	private String notContent;
	private Timestamp notCreatedAt;
	private String notCreatedBy;
	private Timestamp notUpdatedAt;
	private String notUpdatedBy;
	private Timestamp notDeletedAt;
	private String notDeletedBy;
	private int notReadCount;

	public NoticeEntity toEntity() {
		return NoticeEntity.builder()
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
