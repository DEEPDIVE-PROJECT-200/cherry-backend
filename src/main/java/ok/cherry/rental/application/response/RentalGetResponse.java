package ok.cherry.rental.application.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 이용 내역 조회 응답 DTO")
public record RentalGetResponse(

	@Schema(description = "이용 내역(대여) 목록")
	List<RentalInfoResponse> rentals,

	@Schema(description = "다음 페이지 존재 여부")
	boolean hasNext,

	@Schema(description = "마지막 대여 ID")
	Long lastRentalId
) {
}
