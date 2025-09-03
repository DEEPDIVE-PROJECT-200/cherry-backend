package ok.cherry.shipping.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingDetail {

	@Column(nullable = false)
	private LocalDateTime createdAt;

	private LocalDateTime startAt;

	private LocalDateTime endAt;

	static ShippingDetail create() {
		ShippingDetail shippingDetail = new ShippingDetail();
		shippingDetail.createdAt = LocalDateTime.now();
		return shippingDetail;
	}
}