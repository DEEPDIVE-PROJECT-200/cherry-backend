package ok.cherry.product.application.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductCreateRequest(

	@NotBlank(message = "상품명은 필수입니다.")
	String name,

	@NotBlank(message = "브랜드명은 필수입니다.")
	String brand,

	@NotEmpty(message = "최소 하나 이상의 색상 옵션이 필요합니다.")
	List<String> colors,

	@NotNull(message = "일일 대여 가격은 필수입니다")
	@Positive(message = "일일 대여 가격은 양수여야 합니다")
	Long dailyRentalPrice,

	@NotNull(message = "출시일은 필수입니다")
	@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "출시일은 YYYY-MM-DD 형식으로 입력해야 합니다.")
	String launchedAt,

	@NotEmpty(message = "최소 하나 이상의 썸네일 이미지가 필요합니다")
	@Size(max = 10, message = "썸네일 이미지는 최대 10개까지 가능합니다")
	List<String> thumbnailImages,

	@NotEmpty(message = "최소 하나 이상의 상세 이미지가 필요합니다")
	List<String> detailImages

) {
}
