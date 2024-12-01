package org.myweb.first.member.model.dto;

import lombok.*;
import org.myweb.first.member.jpa.entity.MemberEntity;

import javax.management.relation.Role;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Member 클래스는 MEMBER 테이블의 데이터를 전송하기 위한 DTO 클래스입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    private String memUuid; // 회원 고유 식별자

    private String memId; // 회원 아이디

    private String memPw; // 회원 비밀번호

    private String memName; // 회원 이름

    private String memType; // 회원 타입

    private String memEmail; // 회원 이메일

    private String memAddress; // 회원 주소

    private String memCellphone; // 휴대전화번호

    private String memPhone; // 일반전화번호

    private String memRnn; // 주민등록번호

    private String memGovCode; // 관공서코드

    private String memStatus; // 회원 상태

    private Timestamp memEnrollDate; // 가입일자

    private Timestamp memChangeStatus; // 가족계정 승인여부 변경일자

    private String memFamilyApproval; // 가족계정 승인여부

    private String memSocialKakao; // 소셜연동 KAKAO

    private String memKakaoEmail; // KAKAO 이메일

    private String memSocialNaver; // 소셜연동 NAVER

    private String memNaverEmail; // NAVER 이메일

    private String memSocialGoogle; // 소셜연동 GOOGLE

    private String memGoogleEmail; // GOOGLE 이메일

    private String memUuidFam; // 가족 고유 식별자

    private String memUuidMgr; // 담당자 고유 식별자


    public MemberEntity toEntity() {
        return MemberEntity.builder()
                //.memUuid(this.memUuid) // UUID는 @PrePersist에서 자동 생성되므로 포함되지 않음
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
                .memStatus(this.memStatus != null ? this.memStatus : "ACTIVE") // 기본값 설정
                .memFamilyApproval(this.memFamilyApproval != null ? this.memFamilyApproval : "N")
                .memSocialKakao(this.memSocialKakao != null ? this.memSocialKakao : "N")
                .memSocialNaver(this.memSocialNaver != null ? this.memSocialNaver : "N")
                .memSocialGoogle(this.memSocialGoogle != null ? this.memSocialGoogle : "N")
                .memKakaoEmail(this.memKakaoEmail)
                .memNaverEmail(this.memNaverEmail)
                .memGoogleEmail(this.memGoogleEmail)
                .memUuidFam(this.memUuidFam)
                .memUuidMgr(this.memUuidMgr)
                .build();
    }
}
