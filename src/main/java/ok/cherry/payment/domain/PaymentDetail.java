package ok.cherry.payment.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentDetail {

	@Column(nullable = false)
	private LocalDateTime createdAt;

	private LocalDateTime completedAt;

	static PaymentDetail create() {
		PaymentDetail paymentDetail = new PaymentDetail();
		paymentDetail.createdAt = LocalDateTime.now();
		return paymentDetail;
	}

	void markCompleted() {
		this.completedAt = LocalDateTime.now();
	}
}