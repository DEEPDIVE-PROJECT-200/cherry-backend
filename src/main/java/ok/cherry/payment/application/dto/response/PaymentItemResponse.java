package ok.cherry.payment.application.dto.response;

import java.math.BigDecimal;

import ok.cherry.product.domain.type.Brand;
import ok.cherry.product.domain.type.Color;

public record PaymentItemResponse(
	String productName,
	Brand brand,
	Color color,
	Integer quantity,
	BigDecimal price
) {
}
