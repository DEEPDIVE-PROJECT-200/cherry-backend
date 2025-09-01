package ok.cherry.shipping.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

	private String postcode;
	private String postAddress;
	private String detailAddress;

	public Address(String postcode, String postAddress, String detailAddress) {
		this.postcode = postcode;
		this.postAddress = postAddress;
		this.detailAddress = detailAddress;
	}
}