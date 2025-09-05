package ok.cherry.payment.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import ok.cherry.payment.domain.status.PaymentStatus;
import ok.cherry.payment.domain.type.PaymentMethod;

public record PaymentResponse(
	Long paymentId,
	Long memberId,
	Long rentalId,
	BigDecimal totalAmount,
	PaymentMethod paymentMethod,
	PaymentStatus paymentStatus,
	LocalDateTime rentalStartAt,
	LocalDateTime rentalEndAt,
	List<PaymentItemResponse> items,
	AdditionalFeeResponse additionalFee
) {
}
