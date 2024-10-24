package org.myweb.first.notice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.myweb.first.notice.jpa.entity.NoticeEntity;

import java.util.Date;

@Data //@Getter, @Setter, toString, equals, hashCode 오버라이딩 까지 자동 생성됨
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notice {
	//@NotBlank
	// String, CharSequence 타입에만 적용할 수 있음
	//빈 문자열 또는 공백 문자열을 허용하지 않음
	private int noticeNo;			//NOTICENO	NUMBER
	@NotBlank
	private String noticeTitle;		//NOTICETITLE	VARCHAR2(50 BYTE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date noticeDate;	//NOTICEDATE	DATE
	private String noticeWriter;		//NOTICEWRITER	VARCHAR2(50 BYTE)
	private String noticeContent;		//NOTICECONTENT	VARCHAR2(2000 BYTE)
	private String originalFilePath;	//ORIGINAL_FILEPATH	VARCHAR2(100 BYTE)
	private String renameFilePath;	//RENAME_FILEPATH	VARCHAR2(100 BYTE)
	private String importance;		//IMPORTANCE	CHAR(1 BYTE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String impEndDate;	//IMP_END_DATE	DATE
	private int readCount;			//READCOUNT	NUMBER

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
				.impEndDate(java.sql.Date.valueOf(impEndDate))
				.readCount(readCount)
				.build();
	}//toEntity() end

}//Notice end
