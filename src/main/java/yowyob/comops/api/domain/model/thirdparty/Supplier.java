package yowyob.comops.api.domain.model.thirdparty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Supplier extends ThirdParty {
    private PaymentMode paymentMode;
    private MainProductType mainProductType;
    private String deliveryLeadTime;
    private String certification;
}
