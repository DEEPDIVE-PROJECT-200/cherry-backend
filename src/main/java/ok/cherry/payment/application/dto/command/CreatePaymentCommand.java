package ok.cherry.payment.application.dto.command;

import java.math.BigDecimal;

import ok.cherry.payment.domain.type.PaymentMethod;

public record CreatePaymentCommand(

	PaymentMethod paymentMethod,
	BigDecimal shippingFee,
	BigDecimal cleaningFee
) {

	public static CreatePaymentCommand of(PaymentMethod paymentMethod, BigDecimal shippingFee, BigDecimal cleaningFee) {
		return new CreatePaymentCommand(paymentMethod, shippingFee, cleaningFee);
	}
}
