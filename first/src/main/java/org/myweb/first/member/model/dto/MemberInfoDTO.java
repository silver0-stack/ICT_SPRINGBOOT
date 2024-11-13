package org.myweb.first.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 회원 정보 조회 시 비밀번호를 제외한 정보만 제공하기 위한 DTO*/
@Data  // Lombok을 이용한 Getter, Setter, ToString, Equals, HashCode 자동 생성
@AllArgsConstructor  // 모든 필드를 인자로 받는 생성자 생성
@NoArgsConstructor  // 기본 생성자 생성
@Builder  // 빌더 패턴 지원
public class MemberInfoDTO {
    private String userId;  // 사용자 ID
    private String userName;  // 사용자 이름
    private String gender;  // 성별
    private int age;  // 나이
    private String phone;  // 전화번호
    private String email;  // 이메일
    private java.sql.Date enrollDate;  // 가입일
    private java.sql.Date lastModified;  // 최종 수정일
    private String signType;  // 가입 방식 (예: direct)
    private String adminYN;  // 관리자 여부 (Y/N)
    private String loginOk;  // 로그인 허용 여부 (Y/N)
    private String photoFileName;  // 프로필 사진 파일명
    private String roles;  // 사용자 역할 (예: USER, ADMIN)
}