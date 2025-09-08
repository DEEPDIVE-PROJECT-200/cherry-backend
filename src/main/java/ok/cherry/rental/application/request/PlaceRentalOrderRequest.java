package ok.cherry.rental.application.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.annotation.Nullable;
import ok.cherry.payment.domain.type.PaymentMethod;
import ok.cherry.product.domain.type.Color;
import ok.cherry.shipping.domain.ShippingInfo;

public record PlaceRentalOrderRequest(

	// 직접 결제용 필드
	@Nullable Long productId,
	@Nullable Color color,

	// 장바구니 결제용 필드
	@Nullable List<Long> cartIds,

	// 공통 필드
	LocalDate rentStartAt,
	LocalDate rentEndAt,
	ShippingInfo shippingInfo,
	PaymentMethod paymentMethod
) {
	public boolean isDirectRental() {
		return productId != null && color != null;
	}

	public boolean isCartRental() {
		return cartIds != null && !cartIds.isEmpty();
	}
}
