package yowyob.comops.api.domain.model.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import yowyob.comops.api.domain.model.shared.TimeInterval;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialOpeningHours {
    private UUID id;
    private UUID agencyId;
    private LocalDate date;
    private String label; // ex: "Jour de l'an", "Inventaire annuel"
    private boolean isClosed;
    
    @Builder.Default
    private List<TimeInterval> intervals = new ArrayList<>();
}
