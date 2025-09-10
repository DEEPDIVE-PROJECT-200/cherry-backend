package ok.cherry.global.swagger.rental;

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
import ok.cherry.rental.application.request.PlaceRentalOrderRequest;
import ok.cherry.rental.application.response.PlaceRentalOrderResponse;
import ok.cherry.rental.application.response.RentalGetResponse;
import ok.cherry.rental.application.response.RentalInfoResponse;

@Tag(name = "Rentals")
public interface RentalControllerDoc {

	@Operation(method = "GET", summary = "사용자 이용내역(대여) 목록 조회", description = "사용자의 이용내역 목록을 조회하며, 커서 기반 페이지네이션을 사용합니다",
		security = {@SecurityRequirement(name = "JWT")}
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200",
			description = "대여 목록 조회 성공: "
				+ "\n- 대여 목록이 존재할 경우 -> rentals 에 대여 목록 리스트 응답 "
				+ "\n- 대여 목록이 존재하지 않을 경우 -> rentals 에 빈 리스트 응답",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalGetResponse.class)))
	})
	ResponseEntity<RentalGetResponse> getRentals(
		@Parameter(description = "페이지네이션 커서. 이전 응답의 `lastRentalId` 값을 전달하면 다음 페이지를 조회합니다") Long lastRentalId,
		@Parameter(description = "한 페이지에 보여줄 상품 개수", schema = @Schema(type = "integer", defaultValue = "2")) int limit,
		@Parameter String providerId
	);

	@Operation(method = "GET", summary = "사용자 이용내역(대여) 상세 조회", description = "사용자의 이용내역을 상세 조회합니다",
		security = {@SecurityRequirement(name = "JWT")}
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200",
			description = "대여 상세 조회 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalInfoResponse.class))),
		@ApiResponse(responseCode = "401",
			description = "인증되지 않은 사용자",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
		@ApiResponse(responseCode = "403",
			description = "접근 권한이 없는 대여 정보",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
		@ApiResponse(responseCode = "404",
			description = "존재하지 않는 대여 정보",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
	})
	ResponseEntity<RentalInfoResponse> getRental(
		@Parameter(description = "조회할 대여 Id") Long rentalId,
		@Parameter String providerId
	);

	@Operation(
		method = "POST",
		summary = "대여 주문 생성",
		description = """
			새로운 대여 주문을 생성합니다.
					
			**지원하는 결제 방식:**
			- **직접 결제**: productId + color로 단일 상품 대여
			- **장바구니 결제**: cartIds로 장바구니에 담긴 여러 상품 대여
					
			**처리 플로우:**
			1. 대여 생성 (RentalService)
			2. 결제 처리 (PaymentService) 
			3. 배송 생성 (ShippingService)
			4. 장바구니 정리 (장바구니 결제인 경우)
			""",
		security = {@SecurityRequirement(name = "JWT")}
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201",
			description = "대여 주문 생성 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = PlaceRentalOrderResponse.class)
			)
		),
		@ApiResponse(
			responseCode = "400",
			description = """
				대여 주문 생성 실패 - 잘못된 요청:
				- productId와 cartIds가 모두 없음 (INVALID_RENTAL_REQUEST)
				- productId와 cartIds가 모두 있음 (AMBIGUOUS_RENTAL_REQUEST)
				- 잘못된 대여 기간 (시작일 > 종료일)
				- 필수 필드 누락 또는 유효성 검사 실패
				""",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ProblemDetail.class)
			)
		),
		@ApiResponse(
			responseCode = "401",
			description = """
				대여 주문 생성 실패 - 권한 없음:
				- 인증되지 않은 사용자
				- 다른 사용자의 장바구니 접근 시도 (UNAUTHORIZED_CART_ACCESS)
				""",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ProblemDetail.class)
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = """
				대여 주문 생성 실패 - 리소스 없음:
				- 존재하지 않는 상품 ID (PRODUCT_NOT_FOUND)
				- 존재하지 않는 장바구니 ID (CART_NOT_FOUND)
				- 존재하지 않는 사용자 (USER_NOT_FOUND)
				""",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ProblemDetail.class)
			)
		),
		@ApiResponse(
			responseCode = "500",
			description = "서버 내부 오류 - 결제 처리, 배송 생성 등에서 오류 발생",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ProblemDetail.class)
			)
		)
	})
	ResponseEntity<PlaceRentalOrderResponse> placeRentalOrder(
		PlaceRentalOrderRequest request,
		@Parameter String providerId
	);
}
