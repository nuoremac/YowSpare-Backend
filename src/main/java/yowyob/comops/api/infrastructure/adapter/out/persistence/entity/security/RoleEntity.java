package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("roles")
public class RoleEntity {
    @Id
    private UUID id;
    private String name;
    private String description;
}
