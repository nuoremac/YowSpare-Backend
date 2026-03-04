package yowyob.comops.api.domain.model.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private UUID id;
    private String name;
    private String description;
    @Builder.Default
    private List<Permission> permissions = new ArrayList<>();
}