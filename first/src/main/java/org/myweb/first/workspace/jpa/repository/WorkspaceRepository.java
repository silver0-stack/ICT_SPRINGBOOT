package org.myweb.first.workspace.jpa.repository;

import org.myweb.first.workspace.jpa.entity.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, String> {
    Optional<WorkspaceEntity> findByWorkspaceMemUuid(String memUuid);
}
