package org.myweb.first.member.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.member.jpa.entity.MemberEntity;

import java.sql.Date;

@Data  //@Getter, @Setter, @ToString, @Equals, @HashCode 오버라이딩 까지 자동 생성됨
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member {
	@NotBlank
	private String userId;  //USERID	VARCHAR2(50 BYTE)
	private String userPwd; //USERPWD	VARCHAR2(100 BYTE)
	private String userName; //USERNAME	VARCHAR2(20 BYTE)
	private String gender;  //GENDER	CHAR(1 BYTE)
	private int age;   //AGE	NUMBER(3,0)
	private String phone;  //PHONE	VARCHAR2(13 BYTE)
	private String email;   //EMAIL	VARCHAR2(30 BYTE)
	@JsonFormat(pattern="yyyy-MM-dd")
	private java.sql.Date enrollDate;  //ENROLL_DATE	DATE
	@JsonFormat(pattern="yyyy-MM-dd")
	private java.sql.Date lastModified;  //LASTMODIFIED	DATE
	private String signType;  //SIGNTYPE	VARCHAR2(10 BYTE)
	private String adminYN;  //ADMIN_YN	CHAR(1 BYTE)
	private String loginOk;  //LOGIN_OK	CHAR(1 BYTE)
	private String photoFileName;  //PHOTO_FILENAME	VARCHAR2(100 BYTE)
	
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
				.build();
	}
}
