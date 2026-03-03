package yowyob.comops.api.domain.model.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemAudit {
    private UUID id;
    private String user; // Email ou Nom pour affichage rapide
    private String action; // Ex: "USER_LOGIN", "ORGANIZATION_CREATED"
    private String remarks; // Détails de l'action
    private Instant date;
}