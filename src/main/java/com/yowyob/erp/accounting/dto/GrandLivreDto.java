// DTO pour le grand livre
package com.yowyob.erp.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrandLivreDto {

    private String tenantId;
    private String numeroCompte;
    private String libelleCompte;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double soldeInitial;
    private Double soldeFinal;
    private List<GrandLivreLineDto> mouvements;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrandLivreLineDto {
        private LocalDate date;
        private String numeroEcriture;
        private String libelle;
        private Double montantDebit;
        private Double montantCredit;
        private Double soldeProgressive;
    }
}