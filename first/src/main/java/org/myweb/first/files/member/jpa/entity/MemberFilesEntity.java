package org.myweb.first.files.member.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.files.member.model.dto.MemberFiles;
import org.myweb.first.member.jpa.entity.MemberEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "MEMBER_FILES")
public class MemberFilesEntity {
    @Id
    @Column(name = "MF_ID", length = 100, nullable = false)
    private String mfId; // 프로필 사진 고유 식별자 (Primary Key)

    // 다수의 프로필 사진이 하나의 회원에 속할 수 있음을 나타냄
//    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MF_MEM_UUID", nullable = false)
    private String mfMemUuid; // 회원 고유 식별자 (Foreign Key)

    @Column(name = "MF_ORIGINAL_NAME", length = 1000, nullable = false)
    @NotBlank(message = "원본 파일 이름은 필수 입력 항목입니다.")
    private String mfOriginalName; // 원본 파일 이름

    @Column(name = "MF_RENAME", length = 1000, nullable = false)
    @NotBlank(message = "저장용 파일 이름은 필수 입력 항목입니다.")
    private String mfRename; // 저장용 파일 이름

    public MemberFiles toDto(){
        return MemberFiles.builder()
               .mfId(mfId)
               .mfMemUuid(mfMemUuid)
               .mfOriginalName(mfOriginalName)
               .mfRename(mfRename)
               .build();
    }
}
