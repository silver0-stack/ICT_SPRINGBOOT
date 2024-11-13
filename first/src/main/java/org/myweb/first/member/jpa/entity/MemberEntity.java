package org.myweb.first.member.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.member.model.dto.Member;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

/*회원 엔터티 클래스 (DB 테이블과 매핑)*/


@Data  // Lombok을 이용한 Getter, Setter, ToString, Equals, HashCode 자동 생성
@AllArgsConstructor  // 모든 필드를 인자로 받는 생성자 생성
@NoArgsConstructor  // 기본 생성자 생성
@Builder  // 빌더 패턴 지원
@Table(name = "MEMBER") //매핑할 테이블 이름 지정함, NOTICE 테이블을 자동으로 만들어 주기도 하는 어노테이션임
@Entity //JPA 엔터티로 등록
public class MemberEntity {
    @Id
    @Column(name = "USERID", nullable = false)
    private String userId;  //USERID	VARCHAR2(50 BYTE)
    @Column(name = "USERPWD")
    private String userPwd; //USERPWD	VARCHAR2(100 BYTE)
    @Column(name = "USERNAME", nullable = false)
    private String userName; //USERNAME	VARCHAR2(20 BYTE)
    @Column(name = "GENDER", nullable = false)
    private String gender;  //GENDER	CHAR(1 BYTE)
    @Column(name = "AGE", nullable = false)
    private int age;   //AGE	NUMBER(3,0)
    @Column(name = "PHONE")
    private String phone;  //PHONE	VARCHAR2(13 BYTE)
    @Column(name = "EMAIL", nullable = false)
    private String email;   //EMAIL	VARCHAR2(30 BYTE)
    @Column(name = "ENROLL_DATE", nullable = false)
    private Date enrollDate;  //ENROLL_DATE	DATE
    @Column(name = "LASTMODIFIED", nullable = false)
    private Date lastModified;  //LASTMODIFIED	DATE
    @Column(name = "SIGNTYPE", nullable = false, columnDefinition = "direct")
    private String signType;  //SIGNTYPE	VARCHAR2(10 BYTE)
    @Column(name = "ADMIN_YN", nullable = false, columnDefinition = "N")
    private String adminYN;  //ADMIN_YN	CHAR(1 BYTE)
    @Column(name = "LOGIN_OK", nullable = false, columnDefinition = "Y")
    private String loginOk;  //LOGIN_OK	CHAR(1 BYTE)
    @Column(name = "PHOTO_FILENAME")
    private String photoFileName;  //PHOTO_FILENAME	VARCHAR2(100 BYTE)
    @Column(name = "ROLES")
    private String roles;


    /*
    * 엔터티가 저장되기 전에 실행되는 메소드
    * @PrePersist 어노테이션을 통해 엔터티가 저장되기 전 자동으로 호출됨
    * */

    @PrePersist     //jpa 로 넘어가기 전(sql 에 적용하기 전)에 작동된다는 어노테이션임
    public void prePersist(){
        // 가입일과 최종 수정일을 현재 시간으로 설정
        enrollDate = new Date(System.currentTimeMillis());  //현재 날짜 시간 적용
        lastModified = new Date(System.currentTimeMillis());  //현재 날짜 시간 적용
    }

    /*
    * 엔터티를 DTO로 변환하는 메소드
    * @return 회원 정보 DTO
    * */
    public Member toDto(){
        return Member.builder()
                .userId(userId)
                .userPwd(userPwd)
                .userName(userName)
                .gender(gender)
                .age(age)
                .phone(phone)
                .email(email)
                .enrollDate(enrollDate)
                .lastModified(lastModified)
                .signType(signType)
                .adminYN(adminYN)
                .loginOk(loginOk)
                .photoFileName(photoFileName)
                .roles(roles)
                .build();
    }
}
