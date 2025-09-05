package ok.cherry.global.swagger.cart;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ok.cherry.cart.application.dto.request.CartCreateRequest;
import ok.cherry.cart.application.dto.request.CartDeleteRequest;
import ok.cherry.cart.application.dto.response.CartCreateResponse;

@Tag(name = "Carts", description = "🛒 장바구니 API - 장바구니 조회, 관리 API")
public interface CartControllerDoc {

	@Operation(method = "POST", summary = "장바구니 상품 추가", description = "장바구니에 상품을 추가합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "장바구니 상품 등록 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartCreateResponse.class))),
		@ApiResponse(responseCode = "404", description = "장바구니 상품 등록 실패: \n- 사용자 정보를 찾을 수 없음\n- 상품 정보를 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
	})
	ResponseEntity<CartCreateResponse> createCart(CartCreateRequest request, String providerId);

	@Operation(method = "DELETE", summary = "장바구니 상품 삭제", description = "장바구니에 담긴 상품을 삭제합니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "장바구니 상품 삭제 성공"),
		@ApiResponse(responseCode = "401", description = "장바구니 상품 삭제 실패 - 요청에 대한 장바구니 상품 소유자가 일치하지 않음",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
		@ApiResponse(responseCode = "404", description = "장바구니 상품 삭제 실패 - 장바구니 상품을 찾을 수 없음",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
		@ApiResponse(responseCode = "500", description = "장바구니 상품 삭제 실패 - 장바구니 상품 삭제 실패",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
	})
	ResponseEntity<Void> deleteCart(CartDeleteRequest request, String providerId);
}
