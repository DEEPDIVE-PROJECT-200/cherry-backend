package ok.cherry.payment.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.domain.Member;
import ok.cherry.payment.application.dto.command.CreatePaymentCommand;
import ok.cherry.payment.application.dto.response.PaymentResponse;
import ok.cherry.payment.domain.Payment;
import ok.cherry.payment.exception.PaymentError;
import ok.cherry.payment.infrastructure.PaymentRepository;
import ok.cherry.rental.domain.Rental;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;

	public PaymentResponse getPayment(Long paymentId, String providerId) {
		Payment payment = paymentRepository.findByIdWithItems(paymentId)
			.orElseThrow(() -> new BusinessException(PaymentError.PAYMENT_NOT_FOUND));

		validatePaymentOwnership(providerId, payment);

		return PaymentResponse.of(payment);
	}

	public PaymentResponse getPaymentByRentalId(Long rentalId, String providerId) {
		Payment payment = paymentRepository.findByRentalId(rentalId)
			.orElseThrow(() -> new BusinessException(PaymentError.PAYMENT_NOT_FOUND));

		validatePaymentOwnership(providerId, payment);
		return PaymentResponse.of(payment);
	}

	@Transactional
	public Payment createPayment(
		Member member,
		Rental rental,
		CreatePaymentCommand command
	) {
		Payment payment = Payment.create(
			member,
			rental,
			command.paymentMethod(),
			command.shippingFee(),
			command.cleaningFee()
		);
		callExternalPaymentGateway(payment);
		return paymentRepository.save(payment);
	}

	public Payment getPaymentByRentalId(Long rentalId) {
		return paymentRepository.findByRentalId(rentalId)
			.orElseThrow(() -> new BusinessException(PaymentError.PAYMENT_NOT_FOUND));
	}

	/**
	 * 외부 결제 API 호출 로직
	 * 현재는 단순화하여 항상 성공으로 처리
	 * @param payment
	 */
	private void callExternalPaymentGateway(Payment payment) {
		log.info("외부 PG사 결제 처리 완료 - 결제 수단: {}, 결제 금액: {}",
			payment.getPaymentInfo().getPaymentMethod(),
			payment.getPaymentAmount().getTotalAmount());

		payment.complete();
	}

	/**
	 * 결제 정보 조회 권한이 있는지 확인
	 * @param providerId
	 * @param payment
	 */
	private static void validatePaymentOwnership(String providerId, Payment payment) {
		if (!payment.getMember().getProviderId().equals(providerId)) {
			throw new BusinessException(PaymentError.PAYMENT_ACCESS_DENIED);
		}
	}
}
