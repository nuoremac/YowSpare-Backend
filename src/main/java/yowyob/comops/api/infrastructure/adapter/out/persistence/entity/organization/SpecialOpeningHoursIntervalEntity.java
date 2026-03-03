package yowyob.comops.api.infrastructure.adapter.out.persistence.entity.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("special_opening_hours_intervals")
public class SpecialOpeningHoursIntervalEntity {
    @Id
    private UUID id;
    
    @Column("special_id")
    private UUID specialId;
    
    @Column("start_time")
    private LocalTime startTime;

    @Column("end_time")
    private LocalTime endTime;
}
