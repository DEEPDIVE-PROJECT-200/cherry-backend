package ok.cherry.shipping.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingInfo {

	@Column(nullable = false)
	private String receiver;

	@Column(nullable = false)
	private String phoneNumber;

	@Embedded
	private Address address;

	static ShippingInfo create(String receiver, String phoneNumber, Address address) {
		ShippingInfo shippingInfo = new ShippingInfo();
		shippingInfo.receiver = receiver;
		shippingInfo.phoneNumber = phoneNumber;
		shippingInfo.address = address;
		return shippingInfo;
	}
}