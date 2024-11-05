package org.myweb.first.notice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.myweb.first.notice.jpa.entity.NoticeEntity;

import java.util.Date;

@Data  //@Getter, @Setter, @ToString, @Equals, @HashCode 오버라이딩 까지 자동 생성됨
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notice {
	@NotBlank
	private int noticeNo;
	@NotBlank
	private String noticeTitle;
	private java.sql.Date noticeDate;
	private String noticeWriter;
	private String noticeContent;
	private String originalFilePath;
	private String renameFilePath;
	private String importance;
	private java.sql.Date impEndDate;
	private int readCount;

	public NoticeEntity toEntity() {
		return NoticeEntity.builder()
				.noticeNo(noticeNo)
				.noticeTitle(noticeTitle)
				.noticeDate(noticeDate)
				.noticeWriter(noticeWriter)
				.noticeContent(noticeContent)
				.originalFilePath(originalFilePath)
				.renameFilePath(renameFilePath)
				.importance(importance)
				.impEndDate(impEndDate)
				.readCount(readCount)
				.build();
	}
}
