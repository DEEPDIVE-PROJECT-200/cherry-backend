package ok.cherry.shipping.application.command;

import ok.cherry.shipping.domain.Address;
import ok.cherry.shipping.domain.type.Direction;

public record CreateShippingCommand(

	Direction direction,
	String receiver,
	String phoneNumber,
	Address address
) {

	public static CreateShippingCommand of(
		Direction direction,
		String receiver,
		String phoneNumber,
		Address address
	) {
		return new CreateShippingCommand(direction, receiver, phoneNumber, address);
	}
}
