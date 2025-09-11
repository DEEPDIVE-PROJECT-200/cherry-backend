package ok.cherry.shipping.application.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "운송장 번호 및 발송일 조회 응답 DTO")
public record TrackingNumberResponse(

	@Schema(description = "운송장 번호", example = "25090213363012345678")
	String trackingNumber,

	@Schema(description = "발송일자", example = "2025-09-02")
	LocalDate startAt
) {
	public static TrackingNumberResponse of(String trackingNumber, LocalDateTime startAt) {
		return new TrackingNumberResponse(trackingNumber, startAt.toLocalDate());
	}
}
