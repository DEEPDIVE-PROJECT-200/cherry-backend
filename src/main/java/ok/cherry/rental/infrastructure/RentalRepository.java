package ok.cherry.rental.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import ok.cherry.rental.domain.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}
