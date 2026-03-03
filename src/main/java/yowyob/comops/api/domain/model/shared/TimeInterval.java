package yowyob.comops.api.domain.model.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeInterval {
    private LocalTime startTime;
    private LocalTime endTime;

    public boolean isValid() {
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }
}