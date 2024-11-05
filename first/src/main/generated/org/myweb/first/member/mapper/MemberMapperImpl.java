package org.myweb.first.member.mapper;

import javax.annotation.processing.Generated;
import org.myweb.first.member.jpa.entity.MemberEntity;
import org.myweb.first.member.model.dto.Member;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-05T17:06:51+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Member toDto(Member entity) {
        if ( entity == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.userId( entity.getUserId() );
        member.userPwd( entity.getUserPwd() );
        member.userName( entity.getUserName() );
        member.gender( entity.getGender() );
        member.age( entity.getAge() );
        member.phone( entity.getPhone() );
        member.email( entity.getEmail() );
        member.enrollDate( entity.getEnrollDate() );
        member.lastModified( entity.getLastModified() );
        member.signType( entity.getSignType() );
        member.adminYN( entity.getAdminYN() );
        member.loginOk( entity.getLoginOk() );
        member.photoFileName( entity.getPhotoFileName() );

        return member.build();
    }

    @Override
    public MemberEntity toEntity(Member dto) {
        if ( dto == null ) {
            return null;
        }

        MemberEntity.MemberEntityBuilder memberEntity = MemberEntity.builder();

        memberEntity.userId( dto.getUserId() );
        memberEntity.userPwd( dto.getUserPwd() );
        memberEntity.userName( dto.getUserName() );
        memberEntity.gender( dto.getGender() );
        memberEntity.age( dto.getAge() );
        memberEntity.phone( dto.getPhone() );
        memberEntity.email( dto.getEmail() );
        memberEntity.enrollDate( dto.getEnrollDate() );
        memberEntity.lastModified( dto.getLastModified() );
        memberEntity.signType( dto.getSignType() );
        memberEntity.adminYN( dto.getAdminYN() );
        memberEntity.loginOk( dto.getLoginOk() );
        memberEntity.photoFileName( dto.getPhotoFileName() );

        return memberEntity.build();
    }
}
