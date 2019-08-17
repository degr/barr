package com.scnsoft.permissions.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_permissions")
public class AdditionalPermission implements PersistenceEntity<CompositePermissionId>, Serializable {
    @EmbeddedId
    private CompositePermissionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permission_id")
    private Permission permission;

    @Column(name = "enabled", columnDefinition = "TINYINT", length = 1)
    private boolean isEnabled;
}