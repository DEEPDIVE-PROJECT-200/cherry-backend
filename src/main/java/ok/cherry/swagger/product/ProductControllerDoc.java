package ok.cherry.swagger.product;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ok.cherry.product.application.dto.request.ProductCreateRequest;
import ok.cherry.product.application.dto.response.ProductCreateResponse;

@Tag(name = "Products", description = "🎧 상품 API - 상품 조회, 등록, 수정, 삭제 API")
public interface ProductControllerDoc {

	@Operation(method = "POST", summary = "상품 등록", description = "새로운 상품을 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "상품 등록 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductCreateResponse.class)))
	})
	ResponseEntity<ProductCreateResponse> createProduct(ProductCreateRequest request);
}
