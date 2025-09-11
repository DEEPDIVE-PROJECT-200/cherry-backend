package ok.cherry.rental;

import java.time.LocalDate;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.product.domain.Product;
import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.RentalItem;
import ok.cherry.rental.util.RentalNumberGenerator;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalBuilder {

	public Member member = MemberBuilder.create();
	public List<RentalItem> rentalItems = List.of(RentalItemBuilder.create());
	public String rentalNumber = RentalNumberGenerator.generate();
	public LocalDate startAt = LocalDate.now();
	public LocalDate endAt = LocalDate.now().plusDays(7);

	public static Rental create() {
		return builder().build();
	}

	public static RentalBuilder builder() {
		return new RentalBuilder();
	}

	public Rental build() {
		return Rental.create(member, rentalItems, rentalNumber, startAt, endAt);
	}

	public RentalBuilder withMember(Member member) {
		this.member = member;
		return this;
	}

	public RentalBuilder withProduct(Product product) {
		RentalItem rentalItem = RentalItemBuilder.builder()
			.withProduct(product)
			.build();

		this.rentalItems = List.of(rentalItem);
		return this;
	}

	public RentalBuilder withProducts(List<Product> products) {
		this.rentalItems = products.stream()
			.map(product -> RentalItemBuilder.builder()
				.withProduct(product)
				.build())
			.toList();
		return this;
	}

	public RentalBuilder withRentalItems(List<RentalItem> rentalItems) {
		this.rentalItems = rentalItems;
		return this;
	}

	public RentalBuilder withRentalNumber(String rentalNumber) {
		this.rentalNumber = rentalNumber;
		return this;
	}

	public RentalBuilder withStartAt(LocalDate startAt) {
		this.startAt = startAt;
		return this;
	}

	public RentalBuilder withEndAt(LocalDate endAt) {
		this.endAt = endAt;
		return this;
	}
}
