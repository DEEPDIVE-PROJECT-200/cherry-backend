package ok.cherry.payment.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentAmount {

	@Column(nullable = false)
	private BigDecimal rentalAmount;

	@Column(nullable = false)
	private BigDecimal totalAmount;

	@Embedded
	private AdditionalFee additionalFee;

	public static PaymentAmount create(BigDecimal rentalAmount, BigDecimal shippingFee, BigDecimal cleaningFee) {
		PaymentAmount paymentAmount = new PaymentAmount();
		paymentAmount.rentalAmount = rentalAmount;
		paymentAmount.additionalFee = AdditionalFee.create(shippingFee, cleaningFee);
		paymentAmount.totalAmount = rentalAmount.add(paymentAmount.additionalFee.getTotal());
		return paymentAmount;
	}

	public BigDecimal getAdditionalFeeTotal() {
		return additionalFee.getTotal();
	}
}
