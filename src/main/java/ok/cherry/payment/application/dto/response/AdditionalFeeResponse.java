package ok.cherry.payment.application.dto.response;

import java.math.BigDecimal;

public record AdditionalFeeResponse(
	BigDecimal shippingFee,
	BigDecimal cleaningFee
) {
}
