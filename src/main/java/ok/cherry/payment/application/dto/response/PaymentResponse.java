package ok.cherry.payment.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import ok.cherry.payment.domain.status.PaymentStatus;
import ok.cherry.payment.domain.type.PaymentMethod;

@Schema(description = "결제 정보 응답 DTO")
public record PaymentResponse(

	@Schema(description = "결제 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long paymentId,

	@Schema(description = "회원 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long memberId,

	@Schema(description = "대여 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
	Long rentalId,

	@Schema(description = "총 결제 금액", example = "25000", requiredMode = Schema.RequiredMode.REQUIRED)
	BigDecimal totalAmount,

	@Schema(description = "결제 수단", example = "KAKAO_PAY", requiredMode = Schema.RequiredMode.REQUIRED)
	PaymentMethod paymentMethod,

	@Schema(description = "결제 상태", example = "COMPLETED", requiredMode = Schema.RequiredMode.REQUIRED)
	PaymentStatus paymentStatus,

	@Schema(description = "대여 시작일", example = "2025-09-08", requiredMode = Schema.RequiredMode.REQUIRED)
	LocalDate rentalStartedAt,

	@Schema(description = "대여 종료일", example = "2025-09-15", requiredMode = Schema.RequiredMode.REQUIRED)
	LocalDate rentalEndedAt,

	@Schema(description = "결제 아이템 목록", requiredMode = Schema.RequiredMode.REQUIRED)
	List<PaymentItemResponse> items,

	@Schema(description = "추가 요금 정보", requiredMode = Schema.RequiredMode.REQUIRED)
	AdditionalFeeResponse additionalFee
) {
}
