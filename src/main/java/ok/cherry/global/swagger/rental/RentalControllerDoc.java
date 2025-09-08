package ok.cherry.global.swagger.rental;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ok.cherry.rental.application.response.RentalGetResponse;

@Tag(name = "Rental", description = "🍒 대여 API - 상품 대여, 체험, 회수 API")
public interface RentalControllerDoc {

	@Operation(method = "GET", summary = "사용자 이용내역(대여) 목록 조회", description = "사용자의 이용내역 목록을 조회하며, 커서 기반 페이지네이션을 사용합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "대여 목록 조회 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalGetResponse.class)))
	})
	ResponseEntity<RentalGetResponse> getRentals(
		@Parameter(description = "페이지네이션 커서. 이전 응답의 `lastRentalId` 값을 전달하면 다음 페이지를 조회합니다") Long lastRentalId,
		@Parameter(description = "한 페이지에 보여줄 상품 개수", schema = @Schema(type = "integer", defaultValue = "2")) int limit,
		@Parameter(hidden = true) String providerId
	);
}
