package ok.cherry.rental.application.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "대여 주문 생성 응답 DTO")
public record PlaceRentalOrderResponse(

	@Schema(description = "대여 ID", example = "1")
	Long rentalId,

	@Schema(description = "대여 번호", example = "CH-20240908123456789012")
	String rentalNumber,

	@Schema(description = "결제 ID", example = "1")
	Long paymentId,

	@Schema(description = "배송 ID", example = "1")
	Long shippingId,

	@Schema(description = "총 결제 금액", example = "15000.00")
	BigDecimal totalAmount
) {

	public static PlaceRentalOrderResponse of(
		Long rentalId,
		String rentalNumber,
		Long paymentId,
		Long shippingId,
		BigDecimal totalAmount
	) {
		return new PlaceRentalOrderResponse(rentalId, rentalNumber, paymentId, shippingId, totalAmount);
	}
}
