package yowyob.comops.stock.api.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    private UUID userId;
    private UUID organizationId;
    private UUID agencyId;
    private List<String> permissions;
}