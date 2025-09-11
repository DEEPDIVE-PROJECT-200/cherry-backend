package ok.cherry.rental.application.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import ok.cherry.shipping.domain.ShippingInfo;

@Schema(description = "배송 정보")
public record ShippingInfoRequest(

	@Schema(description = "수령인 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "수령인 이름은 필수입니다")
	String receiver,

	@Schema(description = "연락처 (휴대폰 번호)", example = "010-1234-5678", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "연락처는 필수입니다")
	@Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다 (예: 010-1234-5678)")
	String phoneNumber,

	@Schema(description = "배송 주소 정보", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "배송 주소는 필수입니다")
	@Valid
	AddressRequest address
) {
	public ShippingInfo toDomain() {
		return ShippingInfo.create(receiver, phoneNumber, address.toDomain());
	}
}
