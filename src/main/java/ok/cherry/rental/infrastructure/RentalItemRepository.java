package ok.cherry.rental.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import ok.cherry.rental.domain.RentalItem;

public interface RentalItemRepository extends JpaRepository<RentalItem, Long> {
}
