package ok.cherry.payment.application.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import ok.cherry.payment.domain.AdditionalFee;

@Schema(description = "부가 요금 응답 DTO")
public record AdditionalFeeResponse(

	@Schema(description = "배송비", example = "3000", requiredMode = Schema.RequiredMode.REQUIRED)
	BigDecimal shippingFee,

	@Schema(description = "청소비", example = "2000", requiredMode = Schema.RequiredMode.REQUIRED)
	BigDecimal cleaningFee
) {

	public static AdditionalFeeResponse of(AdditionalFee additionalFee) {
		return new AdditionalFeeResponse(
			additionalFee.getShippingFee(),
			additionalFee.getCleaningFee()
		);
	}
}
