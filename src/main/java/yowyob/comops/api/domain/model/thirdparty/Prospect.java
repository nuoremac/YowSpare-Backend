package yowyob.comops.api.domain.model.thirdparty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Prospect extends ThirdParty {
    private ProspectSource source;
    private ProspectPotential potential;
    private Integer probability;
    private LocalDate conversionDate;
    private String notes;
}
