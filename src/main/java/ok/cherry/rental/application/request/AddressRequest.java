package ok.cherry.rental.application.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import ok.cherry.shipping.domain.Address;

@Schema(description = "주소 정보")
public record AddressRequest(

	@Schema(description = "우편번호 (5자리 숫자)", example = "12345", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "우편번호는 필수입니다")
	@Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다")
	String postcode,

	@Schema(description = "기본 주소", example = "서울시 강남구 테헤란로 123", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "기본 주소는 필수입니다")
	String postAddress,

	@Schema(description = "상세 주소 (동, 호수 등)", example = "456호", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "상세 주소는 필수입니다")
	String detailAddress
) {
	public Address toDomain() {
		return new Address(postcode, postAddress, detailAddress);
	}
}