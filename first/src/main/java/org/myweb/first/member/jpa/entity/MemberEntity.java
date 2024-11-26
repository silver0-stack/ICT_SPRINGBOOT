package org.myweb.first.member.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.member.model.dto.Member;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author <a href="mailto:"
 */
@Data  // Lombok을 이용한 Getter, Setter, ToString, Equals, HashCode 자동 생성
@AllArgsConstructor  // 모든 필드를 인자로 받는 생성자 생성
@NoArgsConstructor  // 기본 생성자 생성
@Builder()  // 빌더 패턴 지원
@Table(name = "MEMBER") //매핑할 테이블 이름 지정함, NOTICE 테이블을 자동으로 만들어 주기도 하는 어노테이션임
@Entity //JPA 엔터티로 등록
public class MemberEntity {
    @Id
    @Column(name = "MEM_UUID", length = 100, nullable = false)
    private String memUuid; // 회원 고유 식별자 (Primary Key)

    @Column(name = "MEM_ID", length = 50, unique = true)
    private String memId; // 회원 아이디

    @Column(name = "MEM_PW", length = 50, nullable = false)
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String memPw; // 회원 비밀번호

    @Column(name = "MEM_NAME", length = 50, nullable = false)
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String memName; // 회원 이름

    @Column(name = "MEM_TYPE", length = 30, nullable = false)
    @NotBlank(message = "회원 타입은 필수 입력 항목입니다.")
    @Pattern(regexp = "SENIOR|MANAGER|FAMILY|ADMIN|AI", message = "MEM_TYPE은 'SENIOR', 'MANAGER', 'FAMILY', 'ADMIN', 'AI' 중 하나여야 합니다.")
    private String memType; // 회원 타입

    @Column(name = "MEM_EMAIL", length = 50)
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String memEmail; // 회원 이메일

    @Column(name = "MEM_ADDRESS", length = 300, nullable = false)
    @NotBlank(message = "주소는 필수 입력 항목입니다.")
    private String memAddress; // 회원 주소

    @Column(name = "MEM_CELLPHONE", length = 50)
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "유효한 휴대전화번호 형식이어야 합니다.")
    private String memCellphone; // 휴대전화번호

    @Column(name = "MEM_PHONE", length = 50)
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "유효한 일반전화번호 형식이어야 합니다.")
    private String memPhone; // 일반전화번호

    @Column(name = "MEM_RNN", length = 50, nullable = false)
    @NotBlank(message = "주민등록번호는 필수 입력 항목입니다.")
    private String memRnn; // 주민등록번호

    @Column(name = "MEM_GOV_CODE", length = 50)
    private String memGovCode; // 관공서코드

    @Column(name = "MEM_STATUS", length = 50, nullable = false)
    @NotBlank(message = "회원 상태는 필수 입력 항목입니다.")
    @Pattern(regexp = "ACTIVE|BLOCKED|INACTIVE|REMOVED", message = "MEM_STATUS는 'ACTIVE', 'BLOCKED', 'INACTIVE', 'REMOVED' 중 하나여야 합니다.")
    private String memStatus; // 회원 상태

    @Column(name = "MEM_ENROLL_DATE", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT SYSDATE")
    private Timestamp memEnrollDate; // 가입일자

    @Column(name = "MEM_CHANGE_STATUS", columnDefinition = "TIMESTAMP DEFAULT SYSDATE")
    private Timestamp memChangeStatus; // 가족계정 승인여부 변경일자

    @Column(name = "MEM_FAMILY_APPROVAL", length = 1, nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Pattern(regexp = "Y|N", message = "MEM_FAMILY_APPROVAL은 'Y' 또는 'N'이어야 합니다.")
    private String memFamilyApproval; // 가족계정 승인여부

    @Column(name = "MEM_SOCIAL_KAKAO", length = 1, nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Pattern(regexp = "Y|N", message = "MEM_SOCIAL_KAKAO는 'Y' 또는 'N'이어야 합니다.")
    private String memSocialKakao; // 소셜연동 KAKAO

    @Column(name = "MEM_KAKAO_EMAIL", length = 50)
    @Email(message = "유효한 KAKAO 이메일 형식이어야 합니다.")
    private String memKakaoEmail; // KAKAO 이메일

    @Column(name = "MEM_SOCIAL_NAVER", length = 1, nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Pattern(regexp = "Y|N", message = "MEM_SOCIAL_NAVER는 'Y' 또는 'N'이어야 합니다.")
    private String memSocialNaver; // 소셜연동 NAVER

    @Column(name = "MEM_NAVER_EMAIL", length = 50)
    @Email(message = "유효한 NAVER 이메일 형식이어야 합니다.")
    private String memNaverEmail; // NAVER 이메일

    @Column(name = "MEM_SOCIAL_GOOGLE", length = 1, nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Pattern(regexp = "Y|N", message = "MEM_SOCIAL_GOOGLE는 'Y' 또는 'N'이어야 합니다.")
    private String memSocialGoogle; // 소셜연동 GOOGLE

    @Column(name = "MEM_GOOGLE_EMAIL", length = 50)
    @Email(message = "유효한 GOOGLE 이메일 형식이어야 합니다.")
    private String memGoogleEmail; // GOOGLE 이메일

    @Column(name = "MEM_UUID_FAM", length = 100)
    private String memUuidFam; // 가족 고유 식별자

    @Column(name = "MEM_UUID_MGR", length = 100)
    private String memUuidMgr; // 담당자 고유 식별자


    /*
     * 엔터티를 DTO로 변환하는 메소드
     * @return 회원 정보 DTO
     * */
    public Member toDto() {
        return Member.builder()
                .memUuid(this.memUuid)
                .memId(this.memId)
                .memPw(this.memPw)
                .memName(this.memName)
                .memType(this.memType)
                .memEmail(this.memEmail)
                .memAddress(this.memAddress)
                .memCellphone(this.memCellphone)
                .memPhone(this.memPhone)
                .memRnn(this.memRnn)
                .memGovCode(this.memGovCode)
                .memStatus(this.memStatus)
                .memEnrollDate(this.memEnrollDate)
                .memChangeStatus(this.memChangeStatus)
                .memFamilyApproval(this.memFamilyApproval)
                .memSocialKakao(this.memSocialKakao)
                .memKakaoEmail(this.memKakaoEmail)
                .memSocialNaver(this.memSocialNaver)
                .memNaverEmail(this.memNaverEmail)
                .memSocialGoogle(this.memSocialGoogle)
                .memGoogleEmail(this.memGoogleEmail)
                .memUuidFam(this.memUuidFam)
                .memUuidMgr(this.memUuidMgr)
                .build();
    }
}
