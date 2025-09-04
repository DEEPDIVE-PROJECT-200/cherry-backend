package ok.cherry.rental.application.dto.request;

import java.time.LocalDate;
import java.util.List;

import ok.cherry.rental.domain.RentalItem;

public record RentalCreateRequest(

	List<RentalItem> items,
	LocalDate rentStartAt,
	LocalDate rentEndAt
) {
}
