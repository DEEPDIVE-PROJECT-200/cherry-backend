package ok.cherry.shipping.application.command;

import ok.cherry.shipping.domain.Address;
import ok.cherry.shipping.domain.type.Direction;

public record CreateShippingCommand(

	String trackingNumber,
	Direction direction,
	String receiver,
	String phoneNumber,
	Address address
) {
}
