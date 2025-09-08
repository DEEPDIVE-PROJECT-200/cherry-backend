package ok.cherry.payment.domain;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.payment.PaymentBuilder;
import ok.cherry.payment.domain.status.PaymentStatus;
import ok.cherry.payment.domain.type.PaymentMethod;
import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.domain.Rental;

class PaymentTest {

	@Test
	@DisplayName("결제 생성 시 생성일자가 설정된다")
	void setCreatedDateWhenPaymentIsCreated() {
		// given
		Member member = MemberBuilder.create();
		Rental rental = RentalBuilder.create();
		PaymentMethod paymentMethod = PaymentMethod.KAKAO_PAY;
		BigDecimal shippingFee = BigDecimal.valueOf(3000);
		BigDecimal cleaningFee = BigDecimal.valueOf(2000);

		// when
		Payment payment = Payment.create(member, rental, paymentMethod, shippingFee, cleaningFee);

		// then
		assertThat(payment.getDetail().getCreatedAt()).isNotNull();
	}

	@Test
	@DisplayName("결제 생성 시 기본 상태는 PENDING이다")
	void setDefaultStatusAsPendingWhenPaymentIsCreated() {
		// given
		Member member = MemberBuilder.create();
		Rental rental = RentalBuilder.create();
		PaymentMethod paymentMethod = PaymentMethod.KAKAO_PAY;
		BigDecimal shippingFee = BigDecimal.valueOf(3000);
		BigDecimal cleaningFee = BigDecimal.valueOf(2000);

		// when
		Payment payment = Payment.create(member, rental, paymentMethod, shippingFee, cleaningFee);

		// then
		assertThat(payment.getPaymentInfo().getStatus()).isEqualTo(PaymentStatus.PENDING);
		assertThat(payment.getPaymentInfo().isPending()).isTrue();
		assertThat(payment.isCompleted()).isFalse();
	}

	@Test
	@DisplayName("결제 완료 처리 시 상태가 COMPLETED로 변경되고 완료일자가 설정된다")
	void changeStatusToCompletedAndSetCompletedDateWhenPaymentIsCompleted() {
		// given
		Payment payment = PaymentBuilder.create();

		// when
		payment.complete();

		// then
		assertThat(payment.getPaymentInfo().getStatus()).isEqualTo(PaymentStatus.COMPLETED);
		assertThat(payment.getPaymentInfo().isCompleted()).isTrue();
		assertThat(payment.isCompleted()).isTrue();
		assertThat(payment.getDetail().getCompletedAt()).isNotNull();
	}

	@Test
	@DisplayName("결제 생성 시 총 금액이 대여금액 + 배송비 + 청소비로 계산된다")
	void calculateTotalAmountAsRentalPlusShippingPlusCleaningFee() {
		// given
		Member member = MemberBuilder.create();
		Rental rental = RentalBuilder.builder().withMember(member).build();
		BigDecimal rentalAmount = rental.getTotalPrice();
		BigDecimal shippingFee = BigDecimal.valueOf(3000);
		BigDecimal cleaningFee = BigDecimal.valueOf(2000);
		BigDecimal expectedTotal = rentalAmount.add(shippingFee).add(cleaningFee);

		// when
		Payment payment = Payment.create(member, rental, PaymentMethod.KAKAO_PAY, shippingFee, cleaningFee);

		// then
		assertThat(payment.getPaymentAmount().getTotalAmount()).isEqualTo(expectedTotal);
		assertThat(payment.getPaymentAmount().getRentalAmount()).isEqualTo(rentalAmount);
		assertThat(payment.getPaymentAmount().getAdditionalFee().getShippingFee()).isEqualTo(shippingFee);
		assertThat(payment.getPaymentAmount().getAdditionalFee().getCleaningFee()).isEqualTo(cleaningFee);
	}

	@Test
	@DisplayName("결제 생성 시 대여 기간이 설정된다")
	void setRentalPeriodWhenPaymentIsCreated() {
		// given
		LocalDate startAt = LocalDate.of(2025, 9, 8);
		LocalDate endAt = LocalDate.of(2025, 9, 15);

		Member member = MemberBuilder.create();
		Rental rental = RentalBuilder.builder()
			.withMember(member)
			.withStartAt(startAt)
			.withEndAt(endAt)
			.build();

		// when
		Payment payment = PaymentBuilder.builder()
			.withMember(member)
			.withRental(rental)
			.build();

		// then
		assertThat(payment.getRentalPeriod().getStartedAt()).isEqualTo(startAt);
		assertThat(payment.getRentalPeriod().getEndedAt()).isEqualTo(endAt);
		assertThat(payment.getRentalPeriod().getDays()).isEqualTo(7);
	}

	@Test
	@DisplayName("결제 생성 시 대여 아이템들로부터 결제 아이템들이 생성된다")
	void createPaymentItemsFromRentalItemsWhenPaymentIsCreated() {
		// given
		Member member = MemberBuilder.create();
		Rental rental = RentalBuilder.builder()
			.withMember(member)
			.build();

		// when
		Payment payment = PaymentBuilder.builder()
			.withMember(member)
			.withRental(rental)
			.build();

		// then
		assertThat(payment.getPaymentItems()).hasSize(rental.getRentalItems().size());

		// 첫 번째 아이템 검증
		var firstRentalItem = rental.getRentalItems().getFirst();
		var firstPaymentItem = payment.getPaymentItems().getFirst();

		assertThat(firstPaymentItem.getProductName()).isEqualTo(firstRentalItem.getProduct().getName());
		assertThat(firstPaymentItem.getBrand()).isEqualTo(firstRentalItem.getProduct().getBrand());
		assertThat(firstPaymentItem.getColor()).isEqualTo(firstRentalItem.getColor());
		assertThat(firstPaymentItem.getPrice()).isEqualTo(firstRentalItem.getPrice());
	}

	@Test
	@DisplayName("결제 방법을 지정하여 결제를 생성할 수 있다")
	void createPaymentWithSpecifiedPaymentMethod() {
		// given
		PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

		// when
		Payment payment = PaymentBuilder.builder()
			.withPaymentMethod(paymentMethod)
			.build();

		// then
		assertThat(payment.getPaymentInfo().getPaymentMethod()).isEqualTo(paymentMethod);
	}

	@Test
	@DisplayName("추가 요금을 지정하여 결제를 생성할 수 있다")
	void createPaymentWithSpecifiedAdditionalFee() {
		// given
		BigDecimal shippingFee = BigDecimal.valueOf(5000);
		BigDecimal cleaningFee = BigDecimal.valueOf(3000);

		// when
		Payment payment = PaymentBuilder.builder()
			.withAdditionalFee(shippingFee, cleaningFee)
			.build();

		// then
		assertThat(payment.getPaymentAmount().getAdditionalFee().getShippingFee()).isEqualTo(shippingFee);
		assertThat(payment.getPaymentAmount().getAdditionalFee().getCleaningFee()).isEqualTo(cleaningFee);
		assertThat(payment.getPaymentAmount().getAdditionalFeeTotal()).isEqualTo(BigDecimal.valueOf(8000));
	}
}