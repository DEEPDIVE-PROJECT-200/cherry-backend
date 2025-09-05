package ok.cherry.rental.application.command;

import java.time.LocalDate;
import java.util.List;

import ok.cherry.rental.domain.RentalItem;

public record CreateRentalCommand(

	List<RentalItem> items,
	LocalDate rentStartAt,
	LocalDate rentEndAt
) {
}
