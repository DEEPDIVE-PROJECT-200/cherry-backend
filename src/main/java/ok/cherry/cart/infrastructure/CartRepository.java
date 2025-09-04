package ok.cherry.cart.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import ok.cherry.cart.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
