package yowyob.comops.api.domain.model.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    private UUID id;
    private String resource; // ex: PRODUCT
    private String action; // ex: CREATE
    private String description;

    public String getAuthority() {
        return resource + ":" + action;
    }
}
