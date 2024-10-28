package org.myweb.first.board.jpa.repository;

import org.myweb.first.board.jpa.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepsitory extends JpaRepository<ReplyEntity, JpaRepository> {
}
