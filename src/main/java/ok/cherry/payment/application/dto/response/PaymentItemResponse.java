package ok.cherry.payment.application.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import ok.cherry.payment.domain.PaymentItem;
import ok.cherry.product.domain.type.Brand;
import ok.cherry.product.domain.type.Color;

@Schema(description = "결제 아이템 응답 DTO")
public record PaymentItemResponse(

	@Schema(description = "상품명", example = "SONY WH-1000XM5", requiredMode = Schema.RequiredMode.REQUIRED)
	String productName,

	@Schema(description = "브랜드", example = "SONY", requiredMode = Schema.RequiredMode.REQUIRED)
	Brand brand,

	@Schema(description = "색상", example = "BLACK", requiredMode = Schema.RequiredMode.REQUIRED)
	Color color,

	@Schema(description = "가격", example = "15000", requiredMode = Schema.RequiredMode.REQUIRED)
	BigDecimal price
) {

	public static PaymentItemResponse of(PaymentItem item) {
		return new PaymentItemResponse(
			item.getProductName(),
			item.getBrand(),
			item.getColor(),
			item.getPrice()
		);
	}
}
