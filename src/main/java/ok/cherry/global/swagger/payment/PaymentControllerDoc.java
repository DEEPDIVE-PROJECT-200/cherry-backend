package ok.cherry.global.swagger.payment;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ok.cherry.payment.application.dto.response.PaymentResponse;

@Tag(name = "Payments")
public interface PaymentControllerDoc {

	@Operation(method = "GET", summary = "결제 정보 조회",
		description = "특정 결제 ID에 해당하는 결제 정보와 결제 아이템들을 조회합니다.",
		security = {@SecurityRequirement(name = "JWT")}
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "결제 정보 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = PaymentResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ProblemDetail.class))),
		@ApiResponse(responseCode = "403", description = "결제 정보 접근 권한 없음 - 본인의 결제가 아님",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ProblemDetail.class))),
		@ApiResponse(responseCode = "404", description = "결제 정보 조회 실패 - 존재하지 않는 결제 ID",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ProblemDetail.class)))
	})
	ResponseEntity<PaymentResponse> getPayment(
		@Parameter(description = "조회할 결제 ID", example = "1", required = true) Long paymentId,
		@Parameter(hidden = true) String providerId
	);
}
