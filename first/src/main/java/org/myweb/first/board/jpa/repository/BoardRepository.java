package org.myweb.first.board.jpa.repository;

import org.myweb.first.board.jpa.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, String> {
}
