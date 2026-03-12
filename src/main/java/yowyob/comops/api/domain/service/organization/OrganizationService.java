package yowyob.comops.api.domain.service.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.organization.Organization;
import yowyob.comops.api.domain.model.organization.OrganizationMember;
import yowyob.comops.api.domain.model.security.User;
import yowyob.comops.api.domain.port.in.organization.OrganizationUseCase;
import yowyob.comops.api.domain.port.out.organization.EmployeeRepositoryPort;
import yowyob.comops.api.domain.port.out.organization.OrganizationRepositoryPort;
import yowyob.comops.api.domain.port.out.security.UserRepositoryPort;
import yowyob.comops.api.infrastructure.adapter.out.persistence.mapper.security.UserMapper;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcRoleRepository;
import yowyob.comops.api.infrastructure.adapter.out.persistence.repository.security.R2dbcUserRepository;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService implements OrganizationUseCase {
    private final OrganizationRepositoryPort organizationRepository;
    private final EmployeeRepositoryPort employeeRepository;
    private final UserRepositoryPort userRepository; // Via le Port pour la sauvegarde propre
    private final R2dbcUserRepository r2dbcUserRepository; // Accès direct pour lecture
    private final R2dbcRoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Mono<Organization> createOrganization(Organization organization, UUID ownerActorId) {
        // 1. Récupérer le User Owner (Lié au BusinessActor)
        return r2dbcUserRepository.findByBusinessActorId(ownerActorId)
                .map(userMapper::toDomain)
                .flatMap(user -> {
                    // Check : L'utilisateur a-t-il déjà une organisation ?
                    // Dans le modèle "Isolation Stricte", un User = 1 Org.
                    if (user.getOrganizationId() != null) {
                        return Mono.error(new IllegalStateException(
                                "User is already attached to an organization. Create a new user for a new organization."));
                    }

                    // LIMITATION DU PLAN
                    if (user.getPlan() == User.UserPlan.FREE_TIER) {
                        return Mono.error(
                                new IllegalStateException("Free Tier cannot create organizations. Upgrade required."));
                    }

                    organization.setBusinessActorId(ownerActorId);
                    organization.setCreatedAt(Instant.now());
                    organization.setActive(true);
                    if (organization.getManagerId() == null)
                        organization.setManagerId(ownerActorId);

                    return organizationRepository.save(organization)
                            .flatMap(savedOrg -> {
                                // 2. ATTACHEMENT DU USER A L'ORGANISATION (Étape critique)
                                user.setOrganizationId(savedOrg.getId());

                                // Sauvegarde du User mis à jour
                                return userRepository.save(user)
                                        .then(linkOwnerToOrgAsMember(savedOrg, user.getId()))
                                        .thenReturn(savedOrg);
                            });
                });
    }

    // Ajoute le créateur comme "Super Admin" dans la table des membres
    private Mono<Void> linkOwnerToOrgAsMember(Organization savedOrg, UUID userId) {
        return roleRepository.findByName("ROLE_ADMIN")
                .switchIfEmpty(Mono.error(new IllegalStateException("System role ROLE_ADMIN missing")))
                .flatMap(adminRole -> {
                    OrganizationMember member = OrganizationMember.builder()
                            .organizationId(savedOrg.getId())
                            .userId(userId)
                            .roleId(adminRole.getId())
                            .isActive(true)
                            .joinedAt(Instant.now())
                            .build();
                    return employeeRepository.saveMember(member);
                })
                .then();
    }

    @Override
    public Flux<Organization> getMyOrganizations(UUID actorId) {
        return organizationRepository.findByBusinessActorId(actorId);
    }

    @Override
    public Mono<Organization> getOrganizationById(UUID organizationId) {
        return organizationRepository.findById(organizationId);
    }

    @Override
    @Transactional
    public Mono<Organization> updateOrganization(UUID organizationId, Organization details, UUID requesterActorId) {
        return organizationRepository.findById(organizationId)
                .flatMap(existing -> {
                    if (!existing.getBusinessActorId().equals(requesterActorId)) {
                        return Mono
                                .error(new IllegalStateException("Only the owner can update the organization profile"));
                    }
                    if (details.getName() != null)
                        existing.setName(details.getName());
                    if (details.getDescription() != null)
                        existing.setDescription(details.getDescription());
                    if (details.getEmail() != null)
                        existing.setEmail(details.getEmail());
                    if (details.getServiceType() != null)
                        existing.setServiceType(details.getServiceType());
                    if (details.getLogoUri() != null)
                        existing.setLogoUri(details.getLogoUri());
                    if (details.getLogoId() != null)
                        existing.setLogoId(details.getLogoId());
                    if (details.getTaxNumber() != null)
                        existing.setTaxNumber(details.getTaxNumber());

                    existing.setUpdatedAt(Instant.now());
                    return organizationRepository.save(existing);
                });
    }

    @Override
    @Transactional
    public Mono<Organization> transferOwnership(UUID organizationId, UUID currentOwnerId, UUID newOwnerId) {
        return organizationRepository.findById(organizationId)
                .flatMap(existing -> {
                    if (!existing.getBusinessActorId().equals(currentOwnerId)) {
                        return Mono.error(new IllegalStateException("Only the current owner can transfer ownership"));
                    }
                    existing.setBusinessActorId(newOwnerId);
                    return organizationRepository.save(existing);
                });
    }
}
