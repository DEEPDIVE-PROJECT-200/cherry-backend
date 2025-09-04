package ok.cherry.rental.application.dto.request;

import java.time.LocalDate;
import java.util.List;

import ok.cherry.payment.domain.type.PaymentMethod;
import ok.cherry.shipping.domain.ShippingInfo;

public record CartRentalProcessRequest(
	String providerId,
	List<Long> cartIds,
	LocalDate rentStartAt,
	LocalDate rentEndAt,
	ShippingInfo shippingInfo,
	PaymentMethod paymentMethod
) {
}
