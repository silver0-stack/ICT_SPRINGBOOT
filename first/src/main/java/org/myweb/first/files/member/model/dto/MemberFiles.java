package org.myweb.first.files.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.files.member.jpa.entity.MemberFilesEntity;

/**
 * ProfilePicture 클래스는 PROFILE_PICTURE 테이블의 데이터를 전송하기 위한 DTO 클래스입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFiles {
    private String mfId; // 프로필 사진 고유 식별자

    private String mfMemUuid; // 회원 고유 식별자

    private String mfOriginalName; // 원본 파일 이름

    private String mfRename; // 저장용 파일 이름

    public MemberFilesEntity toEntity() {
        return MemberFilesEntity.builder()
                .mfId(this.mfId)
                .mfMemUuid(this.mfMemUuid)
                .mfOriginalName(this.mfOriginalName)
                .mfRename(this.mfRename)
                .build();
    }
}
