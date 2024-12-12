package org.myweb.first.workspace.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.workspace.jpa.entity.WorkspaceEntity;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Workspace {
    private String workspaceId; // UUID
    private String workspaceName;
    private Timestamp workspaceCreatedAt;
    private Timestamp workspaceUpdatedAt;
    private Timestamp workspaceDeletedAt;
    private String workspaceMemUuid;
    private String workspaceStatus; // ACTIVE, ARCHIVED, DELETED

    public WorkspaceEntity toEntity() {
        return WorkspaceEntity.builder()
                .workspaceId(workspaceId)
                .workspaceName(workspaceName)
                .workspaceCreatedAt(workspaceCreatedAt)
                .workspaceUpdatedAt(workspaceUpdatedAt)
                .workspaceDeletedAt(workspaceDeletedAt)
                .workspaceMemUuid(workspaceMemUuid)
                .workspaceStatus(workspaceStatus)
                .build();
    }

}
