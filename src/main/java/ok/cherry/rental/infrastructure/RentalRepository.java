package ok.cherry.rental.infrastructure;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ok.cherry.rental.domain.Rental;
import ok.cherry.rental.domain.status.RentalStatus;

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

	@Query("SELECT r FROM Rental r " +
		"LEFT JOIN FETCH r.member " +
		"LEFT JOIN FETCH r.rentalItems ri " +
		"LEFT JOIN FETCH ri.product " +
		"WHERE r.id = :rentalId")
	Optional<Rental> findByIdWithDetails(@Param("rentalId") Long rentalId);

	List<Rental> findByRentalStatusAndDetail_EndAtBefore(RentalStatus rentalStatus, LocalDate today);
}