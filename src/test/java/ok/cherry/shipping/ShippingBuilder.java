package ok.cherry.shipping;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.rental.RentalBuilder;
import ok.cherry.rental.domain.Rental;
import ok.cherry.shipping.domain.Address;
import ok.cherry.shipping.domain.Shipping;
import ok.cherry.shipping.domain.type.Direction;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingBuilder {

	public Member member = MemberBuilder.create();
	public Rental rental = RentalBuilder.builder().withMember(member).build();
	public Direction direction = Direction.OUTBOUND;
	public String receiver = "tester";
	public String phoneNumber = "010-1234-5678";
	public Address address = new Address("12345", "postAddress", "detailAddress");

	public static Shipping create() {
		return builder().build();
	}

	public static ShippingBuilder builder() {
		return new ShippingBuilder();
	}

	public Shipping build() {
		return Shipping.create(member, rental, direction, receiver, phoneNumber, address);
	}

	public ShippingBuilder withRental(Rental rental) {
		this.rental = rental;
		return this;
	}

	public ShippingBuilder withDirection(Direction direction) {
		this.direction = direction;
		return this;
	}

	public ShippingBuilder withReceiver(String receiver) {
		this.receiver = receiver;
		return this;
	}

	public ShippingBuilder withPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public ShippingBuilder withAddress(Address address) {
		this.address = address;
		return this;
	}
}