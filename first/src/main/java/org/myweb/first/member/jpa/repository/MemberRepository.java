package org.myweb.first.member.jpa.repository;

import org.myweb.first.member.jpa.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String> {
}
