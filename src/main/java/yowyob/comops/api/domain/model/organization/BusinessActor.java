package yowyob.comops.api.domain.model.organization;

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
public class BusinessActor {
    private UUID id;
    private String name;
    private String businessId; // ID métier lisible
    private String niu;
    private String tradeRegistryNumber;
    private String website;
    private String contactPhone;
    private String privateAddress;
    private String businessAddress;
    private String businessProfile; // Description

    private Instant createdAt;
}
