package yowyob.comops.api.domain.model.thirdparty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartyStatistics {
    private long total;
    private long active;
    private long inactive;
}
