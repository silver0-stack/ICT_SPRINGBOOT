//package org.myweb.first.member.mapper;
//
//import org.mapstruct.Mapper;
//import org.mapstruct.factory.Mappers;
//import org.myweb.first.member.jpa.entity.MemberEntity;
//import org.myweb.first.member.model.dto.Member;
//
///*
//MapStruct를 사용하여 DTO와 엔티티 간의 변환을 자동화합니다.
//이는 코드의 중복을 줄이고, 매핑 로직의 오류 가능성을 낮춥니다.
//MemberMapper 인터페이스를 정의하고, MapStruct가 이를 구현하도록 합니다.
//componentModel = "spring"을 설정하여 스프링 빈으로 매퍼를 사용할 수 있게 합니다.
//MemberService에서 매퍼를 주입받아 사용함으로써 코드가 더욱 간결해집니다.
//*/
//@Mapper(componentModel = "spring")
//public interface MemberMapper {
//    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);
//
//    Member toDto(Member entity);
//    MemberEntity toEntity(Member dto);
//}
