package ok.cherry.global.swagger.product;

import java.util.List;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ok.cherry.product.application.dto.request.ProductCreateRequest;
import ok.cherry.product.application.dto.request.ProductSortType;
import ok.cherry.product.application.dto.response.ProductCreateResponse;
import ok.cherry.product.application.dto.response.ProductSearchResponse;
import ok.cherry.product.domain.type.Brand;

@Tag(name = "Products", description = "🎧 상품 API - 상품 조회, 등록, 수정, 삭제 API")
public interface ProductControllerDoc {

	@Operation(method = "POST", summary = "상품 등록", description = "새로운 상품을 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "상품 등록 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductCreateResponse.class))),
		@ApiResponse(responseCode = "400", description = "상품 등록 실패 - 유효성 검사 실패",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
		@ApiResponse(responseCode = "409", description = "상품 등록 실패 - 중복 상품 등록",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
	})
	ResponseEntity<ProductCreateResponse> createProduct(ProductCreateRequest request);

	@Operation(method = "GET", summary = "상품 목록 조회", description = "필터, 정렬 조건에 맞는 상품 목록을 조회합니다. 커서 기반 페이지네이션을 사용합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "상품 목록 조회 성공",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductSearchResponse.class))),
		@ApiResponse(responseCode = "400",
			description = "상품 목록 조회 실패:\n" +
				"- sortType 필드 누락\n" +
				"- 존재하지 않는 정렬 옵션\n" +
				"- 존재하지 않는 브랜드",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
	})
	ResponseEntity<ProductSearchResponse> searchProducts(
		@Parameter(description = "필터링할 브랜드 목록 (여러 개 선택 가능, 없으면 전체 조회)") List<Brand> brands,
		@Parameter(description = "정렬 기준", required = true) ProductSortType sortType,
		@Parameter(description = "페이지네이션 커서. 이전 응답의 `lastProductId` 값을 전달하면 다음 페이지를 조회합니다.") Long lastProductId,
		@Parameter(description = "한 페이지에 보여줄 상품 개수", schema = @Schema(type = "integer", defaultValue = "6")) int limit
	);
}
