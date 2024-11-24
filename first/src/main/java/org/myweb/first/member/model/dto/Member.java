package org.myweb.first.member.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.member.jpa.entity.MemberEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/*
* 회원 정보 DTO (Data Transfer Object)
* 데이터 이동 및 비즈니스 로직 처리에 사용
* */
@Data  //@Getter, @Setter, @ToString, @Equals, @HashCode 오버라이딩 까지 자동 생성됨
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 생성
@NoArgsConstructor // 기본 생성자 생성
@Builder // 빌더 패턴 지원
public class Member {
	@NotBlank(message = "userId는 필수 입력 항목입니다.")  // 빈 값일 수 없음 (유효성 검사)
	private String userId;  // 사용자 ID, DB 컬럼: USERID, 타입: VARCHAR2(50 BYTE)
	@NotBlank(message="비밀번호는 필수 입력 항목입니다.")
	@Size(min=8, message="비밀번호는 최소 8자리 이상이어야 합니다.")
	private String userPwd; // 사용자 비밀번호, DB 컬럼: USERPWD, 타입: VARCHAR2(100 BYTE)+
	@NotBlank(message="이름은 필수 입력 항목입니다.")
	private String userName; // 사용자 이름, DB 컬럼: USERNAME, 타입: VARCHAR2(20 BYTE)
	@NotBlank(message="성별은 필수 입력 항목입니다.")
	private String gender;  // 성별, DB 컬럼: GENDER, 타입: CHAR(1 BYTE)
	@NotBlank(message="성별은 필수 입력 항목입니다.")
	private int age;   // 나이, DB 컬럼: AGE, 타입: NUMBER(3,0)
	@NotBlank(message="성별은 필수 입력 항목입니다.")
	private String phone;  // 전화번호, DB 컬럼: PHONE, 타입: VARCHAR2(13 BYTE)
	@NotBlank(message="성별은 필수 입력 항목입니다.")
	private String email;   // 이메일, DB 컬럼: EMAIL, 타입: VARCHAR2(30 BYTE)

	@JsonFormat(pattern="yyyy-MM-dd")  // JSON 직렬화 시 날짜 형식 지정
	private java.sql.Date enrollDate;  // 가입일, DB 컬럼: ENROLL_DATE, 타입: DATE

	@JsonFormat(pattern="yyyy-MM-dd")
	private java.sql.Date lastModified;  // 최종 수정일, DB 컬럼: LASTMODIFIED, 타입: DATE

	private String signType;  // 가입 방식, DB 컬럼: SIGNTYPE, 타입: VARCHAR2(10 BYTE)
	private String adminYN;  // 관리자 여부, DB 컬럼: ADMIN_YN, 타입: CHAR(1 BYTE)
	private String loginOk;  // 로그인 허용 여부, DB 컬럼: LOGIN_OK, 타입: CHAR(1 BYTE)
	private String photoFileName;  // 프로필 사진 파일명, DB 컬럼: PHOTO_FILENAME, 타입: VARCHAR2(100 BYTE)
	private String roles;  // 사용자 역할, DB 컬럼: ROLES, 타입: VARCHAR2


	/*
	* DTO를 엔터티로 변환하는 메소드
	* @return 회원 엔터티
	*/
	public MemberEntity toEntity() {
		return MemberEntity.builder()
				.userId(this.userId)
				.userPwd(this.userPwd)
				.userName(this.userName)
				.gender(this.gender)
				.age(this.age)
				.phone(this.phone)
				.email(this.email)
				.enrollDate(this.enrollDate)
				.lastModified(this.lastModified)
				.signType(this.signType)
				.adminYN(this.adminYN)
				.loginOk(this.loginOk)
				.photoFileName(this.photoFileName)
				// roles 필드 추가
				.roles(this.roles)
				.build();
	}
}
