package org.myweb.first.member.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//MVC 패턴에서 Model 은 도메인 모델과 비즈니스로직 처리용 모델로 구분이 됨
//dto(data transfer object : 데이터 이동용 객체) == do(domain object : 한개 행의 정보 저장용 객체)
//vo(value object : 값 저장용 객체) == entity
//db 테이블의 각 컬럼값을 저장할 목적의 클래스임 => 도메인 모델이 됨
//테스트웹 계정의 users 테이블의 한 행의 정보를 저장할 클래스임 => 컬럼과 자료형 맞춰서 필드 구성함
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements java.io.Serializable {
	private String userId; //USERID	VARCHAR2(15 BYTE)
	private String userPwd; //USERPWD	VARCHAR2(100 BYTE)
	private String userName; //USERNAME	VARCHAR2(20 BYTE)
}
