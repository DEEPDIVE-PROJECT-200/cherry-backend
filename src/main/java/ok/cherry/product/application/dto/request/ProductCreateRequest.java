package ok.cherry.product.application.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ok.cherry.product.domain.type.Brand;
import ok.cherry.product.domain.type.Color;

@Schema(description = "상품 등록 요청 DTO")
public record ProductCreateRequest(

	@Schema(description = "상품명", example = "WH-1000XM5")
	@NotBlank(message = "상품명은 필수입니다.")
	String name,

	@Schema(description = "브랜드명", example = "SONY")
	@NotNull(message = "브랜드명은 필수입니다.")
	Brand brand,

	@Schema(description = "상품 색상 옵션", example = "[\"BLACK\", \"MIDNIGHT_BLUE\", \"PLATINUM_SILVER\"]")
	@NotNull(message = "최소 하나 이상의 색상 옵션이 필요합니다.")
	List<Color> colors,

	@Schema(description = "일일 대여 가격(원)", example = "5000")
	@NotNull(message = "일일 대여 가격은 필수입니다")
	@Positive(message = "일일 대여 가격은 양수여야 합니다")
	Long dailyRentalPrice,

	@Schema(description = "제품 출시일", example = "2023-01-15")
	@NotNull(message = "출시일은 필수입니다")
	@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "출시일은 YYYY-MM-DD 형식으로 입력해야 합니다.")
	String launchedAt,

	@Schema(description = "상품 썸네일 이미지 URL 목록(최대 10개)",
		example = "[\"21c559ed-e104-44c0-a027-8db0a3036457_01.jpg\", \"f617a6d7-e3bb-484a-9911-cd9b0c4088c0_02.jpg\"]")
	@NotEmpty(message = "최소 하나 이상의 썸네일 이미지가 필요합니다")
	@Size(max = 10, message = "썸네일 이미지는 최대 10개까지 가능합니다")
	List<String> thumbnailImages,

	@Schema(description = "상품 상세 이미지 URL 목록",
		example = "[\"21c559ed-e104-44c0-a027-8db0a3036457_01.jpg\", \"f617a6d7-e3bb-484a-9911-cd9b0c4088c0_02.jpg\"]")
	@NotEmpty(message = "최소 하나 이상의 상세 이미지가 필요합니다")
	List<String> detailImages
) {
}
