package ok.cherry.cart.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ok.cherry.cart.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	List<Cart> findAllByMemberId(Long memberId);
}
