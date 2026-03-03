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
@Table("opening_hours_rules")
public class OpeningHoursRuleEntity {
    @Id
    private UUID id;
    @Column("agency_id")
    private UUID agencyId;
    
    @Column("day_of_week")
    private Integer dayOfWeek; // 0-6 or 1-7, convention à respecter dans l'adapter

    @Column("is_closed")
    private Boolean isClosed;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
