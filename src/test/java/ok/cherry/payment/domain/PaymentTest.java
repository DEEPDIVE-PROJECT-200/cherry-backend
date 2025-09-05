package ok.cherry.payment.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.payment.domain.status.PaymentStatus;
import ok.cherry.payment.domain.type.PaymentMethod;
import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.domain.Rental;

class PaymentTest {

	@Test
	@DisplayName("결제 생성 시 생성일자가 설정된다")
	void createPaymentWithDetail() {
		// given
		Member member = MemberBuilder.create();
		Rental rental = RentalBuilder.create();
		PaymentMethod paymentMethod = PaymentMethod.KAKAO_PAY;

		// when
		Payment payment = Payment.create(member, rental, paymentMethod);

		// then
		assertThat(payment.getDetail().getCreatedAt()).isNotNull();
	}

	@Test
	@DisplayName("결제 생성 시 기본 상태는 PENDING이다")
	void createPaymentWithDefaultStatus() {
		// given
		Member member = MemberBuilder.create();
		Rental rental = RentalBuilder.create();
		PaymentMethod paymentMethod = PaymentMethod.KAKAO_PAY;

		// when
		Payment payment = Payment.create(member, rental, paymentMethod);

		// then
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
	}
}