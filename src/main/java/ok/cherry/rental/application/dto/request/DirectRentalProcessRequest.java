package ok.cherry.rental.application.dto.request;

import java.time.LocalDate;

import ok.cherry.payment.domain.type.PaymentMethod;
import ok.cherry.product.domain.type.Color;
import ok.cherry.shipping.domain.ShippingInfo;

public record DirectRentalProcessRequest(
	String providerId,
	Long productId,
	Color color,
	LocalDate rentStartAt,
	LocalDate rentEndAt,
	ShippingInfo shippingInfo,
	PaymentMethod paymentMethod
) {
}
