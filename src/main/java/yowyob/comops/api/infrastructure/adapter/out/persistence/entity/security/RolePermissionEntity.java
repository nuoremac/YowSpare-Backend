package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("role_permissions")
public class RolePermissionEntity {
    @Column("role_id")
    private UUID roleId;
    @Column("permission_id")
    private UUID permissionId;
}