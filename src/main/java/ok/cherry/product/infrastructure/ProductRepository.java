package ok.cherry.product.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ok.cherry.product.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	boolean existsByName(String name);
	
	@Query("SELECT p FROM Product p " +
		   "LEFT JOIN FETCH p.detail.productThumbnailDetails " +
		   "LEFT JOIN FETCH p.detail.productImageDetails " +
		   "WHERE p.id = :id")
	Optional<Product> findByIdWithDetails(@Param("id") Long id);
}
