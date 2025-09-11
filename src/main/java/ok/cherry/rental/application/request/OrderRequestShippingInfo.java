package ok.cherry.rental.application.request;

import io.swagger.v3.oas.annotations.media.Schema;
import ok.cherry.shipping.domain.Address;

@Schema(description = "배송 정보")
public record OrderRequestShippingInfo(

	@Schema(description = "수령인", example = "홍길동")
	String receiver,

	@Schema(description = "연락처", example = "010-1234-5678")
	String phoneNumber,

	@Schema(description = "배송 주소")
	Address address
) {
}
