package yowyob.comops.api.domain.service.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.Agency;
import yowyob.comops.api.domain.model.security.User;
import yowyob.comops.api.domain.port.in.organization.AgencyUseCase;
import yowyob.comops.api.domain.port.out.organization.AgencyRepositoryPort;
import yowyob.comops.api.domain.port.out.organization.OrganizationRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.security.UserMapper;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcUserRepository;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgencyService implements AgencyUseCase {
    private final AgencyRepositoryPort agencyRepository;
    private final OrganizationRepositoryPort organizationRepository;
    private final R2dbcUserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Flux<Agency> getAllWarehouses(UUID organizationId) {
        return agencyRepository.findAllWarehouses(organizationId);
    }

    @Override
    public Flux<Agency> getAllAgencies(UUID organizationId) {
        return agencyRepository.findAllByOrganizationId(organizationId);
    }

    @Override
    @Transactional
    public Mono<Agency> createAgency(Agency agency) {
        // Validation des quotas du Plan
        return organizationRepository.findById(agency.getOrganizationId())
                .flatMap(org -> userRepository.findByBusinessActorId(org.getBusinessActorId()))
                .map(userMapper::toDomain)
                .flatMap(ownerUser -> {
                    if (ownerUser.getPlan() == User.UserPlan.FREE_TIER) {
                        return Mono.error(new IllegalStateException("Free Tier cannot create agencies."));
                    }

                    return agencyRepository.findAllByOrganizationId(agency.getOrganizationId()).count()
                            .flatMap(count -> {
                                if (ownerUser.getPlan() == User.UserPlan.FREELANCE && count >= 1) {
                                    return Mono.error(new IllegalStateException("Freelance plan limited to 1 Agency."));
                                }
                                return proceedCreateAgency(agency);
                            });
                });
    }

    private Mono<Agency> proceedCreateAgency(Agency agency) {
        if (agency.getType() == null)
            agency.setType("WAREHOUSE");
        if (agency.getIsActive() == null)
            agency.setIsActive(true);
        if (agency.getIsHeadquarter() == null)
            agency.setIsHeadquarter(false);
        if (agency.getCreatedAt() == null)
            agency.setCreatedAt(Instant.now());

        // Unicité du Siège (Headquarter)
        if (Boolean.TRUE.equals(agency.getIsHeadquarter())) {
            return agencyRepository.findHeadquarter(agency.getOrganizationId())
                    .flatMap(existing -> Mono
                            .<Agency>error(new IllegalArgumentException("Active Headquarter already exists")))
                    .switchIfEmpty(Mono.defer(() -> agencyRepository.save(agency)));
        } else {
            return agencyRepository.save(agency);
        }
    }

    @Override
    @Transactional
    public Mono<Agency> updateAgency(UUID id, Agency incomingData) {
        return agencyRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Agency not found")))
                .flatMap(existing -> {
                    if (incomingData.getName() != null)
                        existing.setName(incomingData.getName());
                    if (incomingData.getLocation() != null)
                        existing.setLocation(incomingData.getLocation());
                    if (incomingData.getType() != null)
                        existing.setType(incomingData.getType());

                    existing.setUpdatedAt(Instant.now());

                    // Gestion bascule HQ
                    if (Boolean.TRUE.equals(incomingData.getIsHeadquarter())
                            && !Boolean.TRUE.equals(existing.getIsHeadquarter())) {
                        return agencyRepository.findHeadquarter(existing.getOrganizationId())
                                .flatMap(hq -> Mono
                                        .<Agency>error(new IllegalArgumentException("Headquarter already exists")))
                                .switchIfEmpty(agencyRepository.save(existing));
                    }

                    return agencyRepository.save(existing);
                });
    }

    @Override
    public Mono<Void> deleteAgency(UUID id) {
        return agencyRepository.findById(id)
                .flatMap(agency -> {
                    if (Boolean.TRUE.equals(agency.getIsHeadquarter())) {
                        return Mono.error(new IllegalStateException("Cannot delete the Headquarter."));
                    }
                    // Note: Le contrôle de stock (impossible de supprimer si stock > 0)
                    // doit être fait par le service Stock, ou via une vérification R2DBC directe si
                    // couplage lâche accepté.
                    // Ici on reste dans le Core pur.
                    return agencyRepository.deleteById(id);
                });
    }
}