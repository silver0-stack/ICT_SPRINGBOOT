package org.myweb.first.files.member.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.files.member.model.dto.MemberFiles;
import org.myweb.first.member.jpa.entity.MemberEntity;

import java.util.UUID;


/**
 * MemberFilesEntity 클래스는 MEMBER_FILES 테이블과 매핑되는 JPA 엔터티 클래스입니다.
 */
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

    // @JoinColumn: 외래 키 칼럼(MF_MEM_UUID)를 지정하며, unique=true 속성을 추가하여 한 회원당 하나의 프로필 사진만을 가질 수 있도록 한다.
    // member: 관계의 주인으로 MemberEntity에서 member 필드가 관계를 관리하게 된다.
    @OneToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "MF_MEM_UUID", nullable = false, unique = true)
    private MemberEntity member; // 회원 고유 식별자 (Foreign Key)

    @Column(name = "MF_ORIGINAL_NAME", length = 1000, nullable = false)
    @NotBlank(message = "원본 파일 이름은 필수 입력 항목입니다.")
    private String mfOriginalName; // 원본 파일 이름

    @Column(name = "MF_RENAME", length = 1000, nullable = false)
    @NotBlank(message = "저장용 파일 이름은 필수 입력 항목입니다.")
    private String mfRename; // 저장용 파일 이름



    @PrePersist
    public void prePersist() {
        if (this.mfId == null) {
            this.mfId = UUID.randomUUID().toString(); // UUID 값 생성
        }
    }

    public MemberFiles toDto(){
        return MemberFiles.builder()
               .mfId(mfId)
               .mfMemUuid(member.getMemUuid())
               .mfOriginalName(mfOriginalName)
               .mfRename(mfRename)
               .build();
    }
}
