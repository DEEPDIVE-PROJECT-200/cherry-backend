package ok.cherry.product.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import ok.cherry.product.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
