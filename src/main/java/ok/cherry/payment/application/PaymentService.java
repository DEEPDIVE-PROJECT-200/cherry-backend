package ok.cherry.payment.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.domain.Member;
import ok.cherry.payment.application.dto.response.AdditionalFeeResponse;
import ok.cherry.payment.application.dto.response.PaymentItemResponse;
import ok.cherry.payment.application.dto.response.PaymentResponse;
import ok.cherry.payment.domain.Payment;
import ok.cherry.payment.domain.type.PaymentMethod;
import ok.cherry.payment.exception.PaymentError;
import ok.cherry.payment.infrastructure.PaymentRepository;
import ok.cherry.rental.domain.Rental;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;

	public PaymentResponse getPayment(Long paymentId) {
		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new BusinessException(PaymentError.PAYMENT_NOT_FOUND));

		List<PaymentItemResponse> itemResponses = payment.getPaymentItems().stream()
			.map(item -> new PaymentItemResponse(
				item.getProductName(),
				item.getBrand(),
				item.getColor(),
				item.getQuantity(),
				item.getTotalPrice()
			))
			.toList();

		AdditionalFeeResponse additionalFeeResponse = new AdditionalFeeResponse(
			payment.getPaymentAmount().getAdditionalFee().getShippingFee(),
			payment.getPaymentAmount().getAdditionalFee().getCleaningFee()
		);

		return new PaymentResponse(
			payment.getId(),
			payment.getMember().getId(),
			payment.getRental().getId(),
			payment.getPaymentAmount().getTotalAmount(),
			payment.getPaymentInfo().getPaymentMethod(),
			payment.getPaymentInfo().getStatus(),
			payment.getRentalPeriod().getStartedAt(),
			payment.getRentalPeriod().getEndedAt(),
			itemResponses,
			additionalFeeResponse
		);
	}

	@Transactional
	public Payment createPayment(
		Member member,
		Rental rental,
		PaymentMethod paymentMethod,
		BigDecimal shippingFee,
		BigDecimal cleaningFee
	) {
		Payment payment = Payment.create(member, rental, paymentMethod, shippingFee, cleaningFee);
		callExternalPaymentGateway(payment);
		return paymentRepository.save(payment);
	}

	/**
	 * 외부 결제 API 호출 로직
	 * 현재는 단순화하여 항상 성공으로 처리
	 * @param payment
	 */
	private void callExternalPaymentGateway(Payment payment) {
		log.info("외부 PG사 결제 처리 완료 - 결제 수단: {}, 금액: {}",
			payment.getPaymentInfo().getPaymentMethod(),
			payment.getRental().getTotalPrice());

		payment.complete();
	}
}
