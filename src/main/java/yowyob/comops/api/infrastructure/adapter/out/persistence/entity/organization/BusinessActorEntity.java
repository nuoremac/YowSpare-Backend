package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("business_actors")
public class BusinessActorEntity {
    @Id
    private UUID id;
    private String name;
    @Column("business_id")
    private String businessId;
    private String niu;

    @Column("trade_registry_number")
    private String tradeRegistryNumber;

    private String website;

    @Column("contact_phone")
    private String contactPhone;

    @Column("private_address")
    private String privateAddress;

    @Column("business_address")
    private String businessAddress;

    @Column("business_profile")
    private String businessProfile;

    @Column("created_at")
    private Instant createdAt;
}