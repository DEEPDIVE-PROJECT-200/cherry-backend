package ok.cherry.rental.application.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "총 대여 수 및 환급 금액 조회 응답 DTO")
public record RentalCountResponse(

	@Schema(description = "총 대여 수", example = "5")
	int count,

	@Schema(description = "총 환급 금액", example = "30000.00")
	BigDecimal totalRefundPrice
) {
	public static RentalCountResponse of(int count, BigDecimal totalRefundPrice) {
		return new RentalCountResponse(count, totalRefundPrice);
	}
}
