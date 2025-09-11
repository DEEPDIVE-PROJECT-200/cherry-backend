package ok.cherry.payment.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdditionalFee {

	@Column(nullable = false)
	private BigDecimal shippingFee;

	@Column(nullable = false)
	private BigDecimal cleaningFee;

	public static AdditionalFee create(BigDecimal shippingFee, BigDecimal cleaningFee) {
		AdditionalFee fee = new AdditionalFee();
		fee.shippingFee = shippingFee;
		fee.cleaningFee = cleaningFee;
		return fee;
	}

	public BigDecimal getTotal() {
		return shippingFee.add(cleaningFee);
	}
}
