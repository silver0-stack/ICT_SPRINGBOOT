/* 회원 정보 업데이트용 별도의 DTO
* ROLE_USER의 유저가 업데이트 가능한 필드만 포함됨
* 이로써, 불필요한 adminYN, loginOK, signType 등 업데이트되는 것을 방지*/
// src/main/java/org/myweb/first/member/model/dto/MemberUpdateDTO.java
package org.myweb.first.member.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MemberUpdateDTO {
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String userName;

    @NotBlank(message = "성별은 필수 입력 항목입니다.")
    private String gender;

    @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
    @Max(value = 150, message = "나이는 150 이하이어야 합니다.")
    private int age;

    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    private String phone;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    private String photoFileName; // 프로필 사진 파일 (선택적)

    // 비밀번호 변경을 원할 경우 추가 필드
    @Size(min = 8, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
    private String userPwd;
}
