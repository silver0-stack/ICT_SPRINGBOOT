package org.myweb.first.workspace.model.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.workspace.jpa.entity.WorkspaceEntity;
import org.myweb.first.workspace.jpa.repository.WorkspaceRepository;
import org.myweb.first.workspace.model.dto.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkspaceService {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    // 해당 UUID의 워크스페이스 추가하는 메소드
    public Workspace createWorkspace(String memUuid){
        WorkspaceEntity newWorkspaceEntity = WorkspaceEntity.builder()
                .workspaceId(UUID.randomUUID().toString())
                .workspaceName("New Workspace") // 기본 이름
                .workspaceCreatedAt(Timestamp.from(Instant.now()))
                .workspaceMemUuid(memUuid)
                .workspaceStatus("ACTIVE")
                .build();

        WorkspaceEntity savedWorkspaceEntity = workspaceRepository.save(newWorkspaceEntity);
        return savedWorkspaceEntity.toDto(); // DTO로 반환Q
    }


    // 특정 UUID 멤버의 워크스페이스가 존재하는지 조회
    public Optional<Workspace> getWorkspaceByMemUuid(String memUuid){
        return workspaceRepository.findByWorkspaceMemUuid(memUuid)
                .map(WorkspaceEntity::toDto);
    }
}
