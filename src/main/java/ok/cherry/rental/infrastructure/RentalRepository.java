package ok.cherry.rental.infrastructure;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ok.cherry.rental.domain.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

	@Query("SELECT r FROM Rental r " +
		"LEFT JOIN FETCH r.member m " +
		"LEFT JOIN FETCH r.rentalItems ri " +
		"LEFT JOIN FETCH ri.product p " +
		"WHERE m.providerId = :providerId " +
		"AND (:lastRentalId IS NULL OR r.id < :lastRentalId) " +
		"ORDER BY r.id DESC")
	List<Rental> findRentalsWithCursorPagination(
		@Param("providerId") String providerId,
		@Param("lastRentalId") Long lastRentalId,
		Pageable pageable
	);
}
