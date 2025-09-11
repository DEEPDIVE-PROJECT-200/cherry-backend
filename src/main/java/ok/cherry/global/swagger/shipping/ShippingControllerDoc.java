package ok.cherry.global.swagger.shipping;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ok.cherry.shipping.application.response.TrackingNumberResponse;

@Tag(name = "Shipping")
public interface ShippingControllerDoc {

	@Operation(method = "GET", summary = "운송장 번호 및 발송일 조회", description = "대여 Id로 배송 정보의 운송장 번호와 발송일을 조회합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "운송장 번호 및 발송일 조회 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrackingNumberResponse.class))),
		@ApiResponse(responseCode = "404", description = "운송장 번호 및 발송일 조회 실패 - 배송 정보를 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
	})
	ResponseEntity<TrackingNumberResponse> getTrackingNumber(
		@Parameter(description = "조회할 대여 Id") Long rentalId
	);
}
