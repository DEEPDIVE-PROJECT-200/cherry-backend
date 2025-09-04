package ok.cherry.shipping.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ok.cherry.global.exception.error.DomainException;
import ok.cherry.member.domain.Member;
import ok.cherry.rental.domain.Rental;
import ok.cherry.shipping.domain.status.ShippingStatus;
import ok.cherry.shipping.domain.type.Direction;
import ok.cherry.shipping.exception.ShippingError;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shipping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rental_id", nullable = false)
	private Rental rental;

	@Column(nullable = false, unique = true)
	private String trackingNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Direction direction;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ShippingStatus status;

	@Embedded
	private ShippingInfo shippingInfo;

	@Embedded
	private ShippingDetail detail;

	public static Shipping create(
		Member member,
		Rental rental,
		Direction direction,
		String receiver,
		String phoneNumber,
		String trackingNumber,
		Address address
	) {
		validateTrackingNumber(trackingNumber);

		Shipping shipping = new Shipping();
		shipping.member = member;
		shipping.rental = rental;
		shipping.direction = direction;
		shipping.shippingInfo = ShippingInfo.create(receiver, phoneNumber, address);
		shipping.trackingNumber = trackingNumber;
		shipping.status = ShippingStatus.PENDING;
		shipping.detail = ShippingDetail.create();
		return shipping;
	}

	private static void validateTrackingNumber(String trackingNumber) {
		if (trackingNumber == null || !trackingNumber.matches("^\\d{20}$")) {
			throw new DomainException(ShippingError.INVALID_TRACKING_NUMBER);
		}
	}
}