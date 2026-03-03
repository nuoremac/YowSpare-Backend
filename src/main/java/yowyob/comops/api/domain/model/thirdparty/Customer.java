package yowyob.comops.api.domain.model.thirdparty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Customer extends ThirdParty {
    private CustomerSegment segment;
    private BigDecimal creditLimit; // plafondCredit
    private AcquisitionChannel acquisitionChannel;
    
    private String customerVatNumber; // numeroTVA specific to client logic if different
    private boolean vatSubject; // estAssujettiTVA
    
    // Sales types
    private boolean retailSale;
    private boolean semiWholesale;
    private boolean wholesale;
    private boolean superWholesale;
    
    private OhadaCustomerType ohadaType;
}
