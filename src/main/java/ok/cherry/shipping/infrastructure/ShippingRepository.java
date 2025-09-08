package ok.cherry.shipping.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ok.cherry.shipping.domain.Shipping;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Long> {
}
