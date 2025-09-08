package ok.cherry.rental.application.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import ok.cherry.payment.domain.type.PaymentMethod;
import ok.cherry.product.domain.type.Color;

@Schema(description = "대여 주문 생성 요청 DTO")
public record PlaceRentalOrderRequest(

	@Schema(description = "직접 결제용 상품 ID", example = "1", nullable = true)
	@Nullable Long productId,

	@Schema(description = "직접 결제용 상품 색상", example = "BLACK", nullable = true)
	@Nullable Color color,

	@Schema(description = "장바구니 결제용 장바구니 ID 목록", example = "[1, 2, 3]", nullable = true)
	@Nullable List<Long> cartIds,

	@Schema(description = "대여 시작일", example = "2025-09-15", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "대여 시작일은 필수입니다")
	@Future(message = "대여 시작일은 오늘 이후의 날짜여야 합니다")
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate rentStartAt,

	@Schema(description = "대여 종료일", example = "2025-09-20", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "대여 종료일은 필수입니다")
	@Future(message = "대여 종료일은 오늘 이후의 날짜여야 합니다")
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate rentEndAt,

	@Schema(description = "배송 정보", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "배송 정보는 필수입니다")
	@Valid
	ShippingInfoRequest shippingInfo,

	@Schema(description = "결제 수단", example = "KAKAO_PAY", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "결제 수단은 필수입니다")
	PaymentMethod paymentMethod
) {
	public boolean isDirectRental() {
		return productId != null && color != null;
	}

	public boolean isCartRental() {
		return cartIds != null && !cartIds.isEmpty();
	}
}
