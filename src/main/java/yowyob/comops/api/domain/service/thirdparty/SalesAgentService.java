package yowyob.comops.api.domain.service.thirdparty;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import yowyob.comops.api.domain.model.thirdparty.SalesAgent;
import yowyob.comops.api.domain.model.thirdparty.ThirdPartyStatistics;
import yowyob.comops.api.domain.port.in.thirdparty.SalesAgentUseCase;
import yowyob.comops.api.domain.port.out.thirdparty.SalesAgentRepositoryPort;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalesAgentService implements SalesAgentUseCase {
    private final SalesAgentRepositoryPort salesAgentRepository;

    @Override
    public Mono<SalesAgent> createAgent(SalesAgent agent) {
        if (agent.getId() == null) {
            agent.setId(UUID.randomUUID());
        }
        agent.setCreatedAt(Instant.now());
        agent.setUpdatedAt(Instant.now());
        agent.setActive(true);
        return salesAgentRepository.save(agent);
    }

    @Override
    public Mono<SalesAgent> updateAgent(UUID id, SalesAgent agent) {
        return salesAgentRepository.findById(id)
                .flatMap(existing -> {
                    agent.setId(id);
                    agent.setCreatedAt(existing.getCreatedAt());
                    agent.setUpdatedAt(Instant.now());
                    return salesAgentRepository.save(agent);
                });
    }

    @Override
    public Mono<SalesAgent> getAgent(UUID id) {
        return salesAgentRepository.findById(id);
    }

    @Override
    public Flux<SalesAgent> getAllAgents(UUID tenantId) {
        return salesAgentRepository.findAllByTenantId(tenantId);
    }

    @Override
    public Mono<Void> deleteSalesAgent(UUID id) {
        return salesAgentRepository.deleteById(id);
    }

    @Override
    public Mono<SalesAgent> findByBankAccountNumber(String bankAccountNumber) {
        return salesAgentRepository.findByBankAccountNumber(bankAccountNumber);
    }

    @Override
    public Mono<SalesAgent> findByAccountingAccount(String accountingAccount) {
        return salesAgentRepository.findByAccountingAccount(accountingAccount);
    }

    @Override
    public Mono<SalesAgent> defineBankAccount(UUID id, String bankAccountNumber) {
        return salesAgentRepository.findById(id)
                .flatMap(agent -> {
                    agent.setBankAccountNumber(bankAccountNumber);
                    agent.setUpdatedAt(Instant.now());
                    return salesAgentRepository.save(agent);
                });
    }

    @Override
    public Mono<SalesAgent> defineAccountingAccount(UUID id, String accountingAccount) {
        return salesAgentRepository.findById(id)
                .flatMap(agent -> {
                    agent.setAccountingAccount(accountingAccount);
                    agent.setUpdatedAt(Instant.now());
                    return salesAgentRepository.save(agent);
                });
    }

    @Override
    public Mono<SalesAgent> activateAgent(UUID id) {
        return salesAgentRepository.findById(id)
                .flatMap(agent -> {
                    agent.setActive(true);
                    agent.setUpdatedAt(Instant.now());
                    return salesAgentRepository.save(agent);
                });
    }

    @Override
    public Mono<SalesAgent> deactivateAgent(UUID id) {
        return salesAgentRepository.findById(id)
                .flatMap(agent -> {
                    agent.setActive(false);
                    agent.setUpdatedAt(Instant.now());
                    return salesAgentRepository.save(agent);
                });
    }

    @Override
    public Mono<ThirdPartyStatistics> getAgentStatistics(UUID tenantId) {
        return salesAgentRepository.getStatistics(tenantId);
    }
}
