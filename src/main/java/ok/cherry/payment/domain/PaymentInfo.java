package ok.cherry.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ok.cherry.payment.domain.status.PaymentStatus;
import ok.cherry.payment.domain.type.PaymentMethod;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentInfo {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentMethod paymentMethod;

	public static PaymentInfo create(PaymentMethod paymentMethod) {
		PaymentInfo paymentInfo = new PaymentInfo();
		paymentInfo.status = PaymentStatus.PENDING;
		paymentInfo.paymentMethod = paymentMethod;
		return paymentInfo;
	}

	public void complete() {
		this.status = PaymentStatus.COMPLETED;
	}

	public boolean isCompleted() {
		return status == PaymentStatus.COMPLETED;
	}

	public boolean isPending() {
		return status == PaymentStatus.PENDING;
	}
}
