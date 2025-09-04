package ok.cherry.rental.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record RentalProcessResponse(
	@Schema(description = "대여 생성 결과 메시지", example = "결제가 성공적으로 완료되었습니다.")
	String message
) {
	public static RentalProcessResponse of() {
		return new RentalProcessResponse("결제가 성공적으로 완료되었습니다.");
	}
}
