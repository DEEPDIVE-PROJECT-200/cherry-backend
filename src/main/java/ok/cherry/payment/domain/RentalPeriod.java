package ok.cherry.payment.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalPeriod {

	@Column(name = "rentalStartedAt", nullable = false)
	private LocalDate startedAt;

	@Column(name = "rentalEndedAt", nullable = false)
	private LocalDate endedAt;

	public static RentalPeriod create(LocalDate startedAt, LocalDate endedAt) {
		RentalPeriod period = new RentalPeriod();
		period.startedAt = startedAt;
		period.endedAt = endedAt;
		return period;
	}

	public long getDays() {
		return startedAt.until(endedAt).getDays();
	}
}
