package ok.cherry.shipping.domain;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ok.cherry.global.exception.error.DomainException;
import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.domain.Rental;
import ok.cherry.shipping.ShippingBuilder;
import ok.cherry.shipping.domain.status.ShippingStatus;
import ok.cherry.shipping.domain.type.Direction;
import ok.cherry.shipping.exception.ShippingError;

class ShippingTest {

	@Test
	@DisplayName("배송 생성 시 생성일자가 설정된다")
	void createShippingWithDetails() {
		// given
		Rental rental = RentalBuilder.create();
		Direction direction = Direction.OUTBOUND;
		String receiver = "tester";
		String phoneNumber = "010-1234-5678";
		String trackingNumber = "25090213363012345678";
		Address address = new Address("12345", "postAddress", "detailAddress");

		// when
		Shipping shipping = Shipping.create(
			rental.getMember(),
			rental,
			trackingNumber,
			direction,
			receiver,
			phoneNumber,
			address
		);

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
		String trackingNumber = "25090213363012345678";
		Address address = new Address("12345", "postAddress", "detailAddress");

		// when
		Shipping shipping = Shipping.create(
			rental.getMember(),
			rental,
			trackingNumber,
			direction,
			receiver,
			phoneNumber,
			address
		);

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
		String trackingNumber = "25090213363012345678";
		Address address = new Address("12345", "postAddress", "detailAddress");

		// when
		Shipping shipping = Shipping.create(
			rental.getMember(),
			rental,
			trackingNumber,
			direction,
			receiver,
			phoneNumber,
			address
		);

		// then
		assertThat(shipping.getShippingInfo().getReceiver()).isEqualTo("tester");
		assertThat(shipping.getShippingInfo().getPhoneNumber()).isEqualTo("010-1234-5678");
		assertThat(shipping.getShippingInfo().getAddress()).isNotNull();
	}

	@Test
	@DisplayName("배송 생성 시 배송번호가 형식이 다르면 예외가 발생한다")
	void createShippingWithTrackingNumber() {
		// given
		String trackingNumber = "wrong_tracking_number";

		// when & then
		assertThatThrownBy(() -> ShippingBuilder.builder().withTrackingNumber(trackingNumber).build())
			.isInstanceOf(DomainException.class)
			.hasMessage(ShippingError.INVALID_TRACKING_NUMBER.getMessage());
	}

	@Test
	@DisplayName("배송 시작 시 배송 상태가 변경되고 배송 시작 시간이 생성된다")
	void startShipping_success() {
		// given
		Shipping shipping = ShippingBuilder.create();

		// when
		shipping.startShipping();

		// then
		assertThat(shipping.getStatus()).isEqualTo(ShippingStatus.IN_DELIVERY);
		assertThat(shipping.getDetail().getStartAt()).isNotNull();
	}

	@Test
	@DisplayName("배송 대기 중 상태가 아닐 때 배송 시작을 하는 경우 예외가 발생한다")
	void startShipping_notPending() {
		// given
		Shipping shipping = ShippingBuilder.create();
		shipping.startShipping();

		// when & then
		assertThatThrownBy(() -> shipping.startShipping())
			.isInstanceOf(DomainException.class)
			.hasMessage(ShippingError.NOT_PENDING.getMessage());
	}

	@Test
	@DisplayName("배송 완료 시 배송 상태가 변경되고 배송 완료 시간이 생성된다")
	void completeShipping_success() {
		// given
		Shipping shipping = ShippingBuilder.create();
		shipping.startShipping();

		// when
		shipping.completeShipping();

		// then
		assertThat(shipping.getStatus()).isEqualTo(ShippingStatus.DELIVERED);
		assertThat(shipping.getDetail().getEndAt()).isNotNull();
	}

	@Test
	@DisplayName("배송 중 상태가 아닐 때 배송 완료를 하는 경우 예외가 발생한다 ")
	void completeShipping_notInDelivery() {
		// given
		Shipping shipping = ShippingBuilder.create();
		shipping.startShipping();
		shipping.completeShipping();

		// when & then
		assertThatThrownBy(() -> shipping.completeShipping())
			.isInstanceOf(DomainException.class)
			.hasMessage(ShippingError.NOT_IN_DELIVERY.getMessage());
	}
}