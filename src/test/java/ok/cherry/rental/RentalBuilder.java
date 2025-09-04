package ok.cherry.rental;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.RentalItem;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalBuilder {

	public Member member = MemberBuilder.create();
	public List<RentalItem> rentalItems = List.of(RentalItemBuilder.create());
	public LocalDateTime startAt = LocalDateTime.now();
	public LocalDateTime endAt = LocalDateTime.now().plusDays(7);

	public static Rental create() {
		return builder().build();
	}

	public static RentalBuilder builder() {
		return new RentalBuilder();
	}

	public Rental build() {
		return Rental.create(member, rentalItems, startAt, endAt);
	}

	public RentalBuilder withMember(Member member) {
		this.member = member;
		return this;
	}

	public RentalBuilder withRentalItems(List<RentalItem> rentalItems) {
		this.rentalItems = rentalItems;
		return this;
	}

	public RentalBuilder withStartAt(LocalDateTime startAt) {
		this.startAt = startAt;
		return this;
	}

	public RentalBuilder withEndAt(LocalDateTime endAt) {
		this.endAt = endAt;
		return this;
	}
}
