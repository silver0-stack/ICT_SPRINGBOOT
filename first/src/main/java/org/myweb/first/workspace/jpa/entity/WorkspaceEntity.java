package org.myweb.first.workspace.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myweb.first.chat.jpa.entity.ChatMessageEntity;

import java.sql.Timestamp;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="WORKSPACE")
public class WorkspaceEntity {
    @Id
    @Column(name = "WORKSPACE_ID", nullable = false, length = 100)
    private String workspaceId;

    @Column(name = "WORKSPACE_NAME", nullable = false, length = 100)
    private String workspaceName;

    @Column(name = "WORKSPACE_CREATED_AT", nullable = false)
    private Timestamp workspaceCreatedAt;

    @Column(name = "WORKSPACE_UPDATED_AT")
    private Timestamp workspaceUpdatedAt;

    @Column(name = "WORKSPACE_MEM_UUID", nullable = false, length = 100)
    private String workspaceMemUuid;

    @Column(name = "WORKSPACE_DELETED_AT")
    private Timestamp workspaceDeletedAt;

    @Column(name = "WORKSPACE_STATUS", nullable = false, length = 20)
    private String workspaceStatus; // ACTIVE, ARCHIVED, DELETED

    @OneToMany(mappedBy = "msgWorkspaceId", cascade=CascadeType.ALL, orphanRemoval=true)
    private Set<ChatMessageEntity> chatMessages;
}
