package ok.cherry.shipping.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ok.cherry.shipping.domain.Shipping;
import ok.cherry.shipping.domain.type.Direction;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {

	Optional<Shipping> findByRentalIdAndDirection(Long rentalId, Direction direction);
}
