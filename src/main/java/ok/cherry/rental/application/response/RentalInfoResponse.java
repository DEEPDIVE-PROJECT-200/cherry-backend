package ok.cherry.rental.application.response;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.status.RentalStatus;

@Schema(description = "대여 정보 조회 DTO")
public record RentalInfoResponse(

	@Schema(description = "대여 ID", example = "1")
	Long rentalId,

	@Schema(description = "대여 번호", example = "CH-25090213363012500001")
	String rentalNumber,

	@Schema(description = "대여 상태", example = "ACTIVE")
	RentalStatus status,

	@Schema(description = "대여 시작일", example = "2025-09-02")
	LocalDate startAt,

	@Schema(description = "대여 종료일", example = "2025-09-09")
	LocalDate endAt,

	@Schema(description = "대여 상품 목록")
	List<RentalItemInfoResponse> items
) {
	public static RentalInfoResponse from(Rental rental) {
		List<RentalItemInfoResponse> items = rental.getRentalItems().stream()
			.map(RentalItemInfoResponse::from)
			.toList();

		return new RentalInfoResponse(
			rental.getId(),
			rental.getRentalNumber(),
			rental.getRentalStatus(),
			rental.getDetail().getStartAt(),
			rental.getDetail().getEndAt(),
			items
		);
	}
}
