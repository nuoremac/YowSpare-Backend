package yowyob.comops.api.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.port.in.config.GeneralOptionsUseCase;
import yowyob.comops.api.domain.port.out.config.SequenceRepositoryPort;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentSequenceService {
    private final SequenceRepositoryPort sequenceRepository;
    private final GeneralOptionsUseCase optionsUseCase;

    /**
     * Génère une référence complète : [ORG]-[AGENCY]-[TYPE]-[YEAR]-[SEQ]
     * Exemple : YOW-DLA-INV-2025-00001
     * 
     * @param organizationId ID de l'organisation (obligatoire)
     * @param agencyId       ID de l'agence (optionnel, si document global ou siège)
     * @param documentType   Type de document (ex: "INV", "ORDER")
     */
    public Mono<String> generateReference(UUID organizationId, UUID agencyId, String documentType) {
        int year = LocalDate.now().getYear();

        // 1. Récupérer les Settings Globaux (Org)
        Mono<String> orgPrefixMono = optionsUseCase.getGlobalSettings(organizationId)
                .map(settings -> settings.getOrganizationPrefix() != null ? settings.getOrganizationPrefix() : "ORG");

        // 2. Récupérer les Settings Locaux (Agence), si agencyId est fourni
        Mono<String> agencyPrefixMono = (agencyId != null)
                ? optionsUseCase.getAgencySettings(agencyId)
                        .map(settings -> settings.getAgencyPrefix() != null ? settings.getAgencyPrefix() : "AGY")
                : Mono.just(""); // Pas de préfixe agence si global

        // 3. Combiner tout ça
        return Mono.zip(orgPrefixMono, agencyPrefixMono)
                .flatMap(tuple -> {
                    String orgPrefix = tuple.getT1();
                    String agyPrefix = tuple.getT2();

                    // Construction du préfixe composé
                    String fullPrefix = orgPrefix;
                    if (!agyPrefix.isEmpty()) {
                        fullPrefix += "-" + agyPrefix;
                    }
                    fullPrefix += "-" + documentType; // ex: YOW-DLA-INV

                    // 4. Incrémenter la séquence
                    // On séquence par (Org, Type, Year) pour garantir l'unicité globale ou par
                    // agence ?
                    // Si on veut des séquences continues par agence (DLA-001, YDE-001), il faudrait
                    // passer agencyId au repo.
                    // Ici on assume une séquence globale par type de document pour l'org, mais
                    // préfixée différemment.
                    // Si besoin de séquences isolées par agence, il faut modifier le
                    // SequenceRepositoryPort.
                    // Restons sur séquence globale Org pour l'instant (plus simple pour la compta
                    // centralisée).
                    String finalPrefix = fullPrefix;
                    return sequenceRepository.getNextValue(organizationId, documentType, year)
                            .map(seq -> String.format("%s-%d-%05d", finalPrefix, year, seq));
                });
    }
}