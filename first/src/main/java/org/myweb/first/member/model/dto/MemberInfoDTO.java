package org.myweb.first.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*비밀번호 출력 안시키기 위한 DTO*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberInfoDTO {
    private String userId;
    private String userName;
    private String gender;
    private int age;
    private String phone;
    private String email;
    private java.sql.Date enrollDate;
    private java.sql.Date lastModified;
    private String signType;
    private String adminYN;
    private String loginOk;
    private String photoFileName;
    private String roles;
}