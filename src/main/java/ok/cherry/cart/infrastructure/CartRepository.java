package ok.cherry.cart.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ok.cherry.cart.domain.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	List<Cart> findAllByMemberId(Long memberId);
	
	@Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.member.id = :memberId")
	List<Cart> findAllByMemberIdWithProduct(Long memberId);
}
