package yowyob.comops.api.domain.model.thirdparty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SalesAgent extends ThirdParty {
    private SalesAgentType agentType;
    private String coveredZones;
    private String specializations;
    private BigDecimal commission;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
}
