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
@Table("permissions")
public class PermissionEntity {
    @Id
    private UUID id;
    private String resource;
    private String action;
    private String description;
}