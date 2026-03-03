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
@Table("user_roles")
public class UserRoleEntity {
    @Column("user_id")
    private UUID userId;
    @Column("role_id")
    private UUID roleId;
}
