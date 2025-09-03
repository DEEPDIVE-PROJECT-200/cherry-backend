package ok.cherry.payment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.payment.domain.Payment;
import ok.cherry.payment.domain.type.PaymentMethod;
import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.domain.Rental;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentBuilder {

	public Member member = MemberBuilder.create();
	public Rental rental = RentalBuilder.builder().withMember(member).build();
	public PaymentMethod paymentMethod = PaymentMethod.KAKAO_PAY;

	public static Payment create() {
		return builder().build();
	}

	public static PaymentBuilder builder() {
		return new PaymentBuilder();
	}

	public Payment build() {
		return Payment.create(member, rental, paymentMethod);
	}

	public PaymentBuilder withMember(Member member) {
		this.member = member;
		return this;
	}

	public PaymentBuilder withRental(Rental rental) {
		this.rental = rental;
		return this;
	}

	public PaymentBuilder withPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
		return this;
	}
}