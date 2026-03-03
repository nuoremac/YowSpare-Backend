package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("special_opening_hours")
public class SpecialOpeningHoursEntity {
    @Id
    private UUID id;
    @Column("agency_id")
    private UUID agencyId;
    private LocalDate date;
    private String label;

    @Column("is_closed")
    private Boolean isClosed;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}