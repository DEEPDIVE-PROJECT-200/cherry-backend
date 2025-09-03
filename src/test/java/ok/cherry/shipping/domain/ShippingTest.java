package ok.cherry.shipping.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.domain.Rental;
import ok.cherry.shipping.domain.status.ShippingStatus;
import ok.cherry.shipping.domain.type.Direction;

class ShippingTest {

	@Test
	@DisplayName("배송 생성 시 생성일자가 설정된다")
	void createShippingWithDetails() {
		// given
		Rental rental = RentalBuilder.create();
		Direction direction = Direction.OUTBOUND;
		String receiver = "tester";
		String phoneNumber = "010-1234-5678";
		Address address = new Address("12345", "postAddress", "detailAddress");

		// when
		Shipping shipping = Shipping.create(rental.getMember(), rental, direction, receiver, phoneNumber, address);

		// then
		assertThat(shipping.getDetail().getCreatedAt()).isNotNull();
	}

	@Test
	@DisplayName("배송 생성 시 기본 상태는 PENDING이다")
	void createShippingWithDefaultStatus() {
		// given
		Rental rental = RentalBuilder.create();
		Direction direction = Direction.OUTBOUND;
		String receiver = "tester";
		String phoneNumber = "010-1234-5678";
		Address address = new Address("12345", "postAddress", "detailAddress");

		// when
		Shipping shipping = Shipping.create(rental.getMember(), rental, direction, receiver, phoneNumber, address);

		// then
		assertThat(shipping.getStatus()).isEqualTo(ShippingStatus.PENDING);
	}

	@Test
	@DisplayName("배송 생성 시 배송정보가 생성된다")
	void createShippingWithShippingInfo() {
		// given
		Rental rental = RentalBuilder.create();
		Direction direction = Direction.OUTBOUND;
		String receiver = "tester";
		String phoneNumber = "010-1234-5678";
		Address address = new Address("12345", "postAddress", "detailAddress");

		// when
		Shipping shipping = Shipping.create(rental.getMember(), rental, direction, receiver, phoneNumber, address);

		// then
		assertThat(shipping.getShippingInfo().getReceiver()).isEqualTo("tester");
		assertThat(shipping.getShippingInfo().getPhoneNumber()).isEqualTo("010-1234-5678");
		assertThat(shipping.getShippingInfo().getAddress()).isNotNull();
	}

	@Test
	@DisplayName("배송 생성 시 배송번호가 자동으로 생성된다")
	void createShippingWithTrackingNumber() {
		// given
		Rental rental = RentalBuilder.create();
		Direction direction = Direction.OUTBOUND;
		String receiver = "tester";
		String phoneNumber = "010-1234-5678";
		Address address = new Address("12345", "postAddress", "detailAddress");

		// when
		Shipping shipping = Shipping.create(rental.getMember(), rental, direction, receiver, phoneNumber, address);

		// then
		assertThat(shipping.getTrackingNumber()).isNotNull();
		assertThat(shipping.getTrackingNumber()).hasSize(20);
	}
}