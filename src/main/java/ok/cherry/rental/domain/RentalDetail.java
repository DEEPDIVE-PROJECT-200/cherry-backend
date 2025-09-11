package ok.cherry.rental.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalDetail {

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDate startAt;

	@Column(nullable = false)
	private LocalDate endAt;

	static RentalDetail create(LocalDate startAt, LocalDate endAt) {
		RentalDetail rentalDetail = new RentalDetail();
		rentalDetail.createdAt = LocalDateTime.now();
		rentalDetail.startAt = startAt;
		rentalDetail.endAt = endAt;
		return rentalDetail;
	}
}