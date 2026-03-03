package yowyob.comops.api.domain.model.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Agrégat pour l'affichage complet du planning d'une agence.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencySchedule {
    private String agencyName;
    private String timezone;
    private List<OpeningHoursRule> regularRules;
    private List<SpecialOpeningHours> upcomingExceptions;
}