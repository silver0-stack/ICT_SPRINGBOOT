package org.myweb.first.files.member.jpa.repository;

import org.myweb.first.files.member.jpa.entity.MemberFilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberFilesRepository extends JpaRepository<MemberFilesEntity, String> {

    /**
     * 회원 UUID로 프로필 사진을 찾는 메소드
     * @param memUuid
     * @return Optional<ProfilePictureEntity>
     */
    Optional<MemberFilesEntity> findByMember_MemUuid(String memUuid);
}
