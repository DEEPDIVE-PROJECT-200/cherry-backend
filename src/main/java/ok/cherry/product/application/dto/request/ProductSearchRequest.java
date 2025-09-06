package ok.cherry.product.application.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ok.cherry.product.domain.type.Brand;

public record ProductSearchRequest(

	@Schema(description = "조회할 브랜드 목록", example = "[\"SONY\", \"APPLE\"]")
	List<Brand> brands,

	@Schema(description = "정렬 조건", example = "REGISTERED",
		allowableValues = {"REGISTERED", "LAUNCHED", "PRICE_ASC", "PRICE_DESC"})
	@NotNull(message = "정렬 조건은 필수입니다")
	ProductSortType sortType
) {
}
