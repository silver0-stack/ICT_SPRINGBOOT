package org.myweb.first.files.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.files.member.jpa.entity.MemberFilesEntity;
import org.myweb.first.member.jpa.entity.MemberEntity;

import java.util.UUID;

/**
 * MemberFiles 클래스는 MEMBER_FILES 테이블의 데이터를 전송하기 위한 DTO 클래스입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFiles {
     private String mfId; // 프로필 사진 고유 식별자(UUID.randomUUID().toString)

     private String mfMemUuid; // 회원 고유 식별자(Member 테이블의 FK 칼럼)

     private String mfOriginalName; // 원본 파일 이름

     private String mfRename; // 저장용 파일 이름


     /**
     * DTO를 Entity로 변환하는 메소드
     * @param member 회원 엔터티 객체
     * @return MemberFilesEntity 객체
     */
    public MemberFilesEntity toEntity(MemberEntity member) {
        return MemberFilesEntity.builder()
                .mfId(this.mfId!=null? this.mfId: UUID.randomUUID().toString())
                .member(member) // MemberEntity 객체 생성
                .mfOriginalName(this.mfOriginalName)
                .mfRename(this.mfRename)
                .build();
    }
}
