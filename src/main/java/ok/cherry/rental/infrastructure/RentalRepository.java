package ok.cherry.rental.infrastructure;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
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

	List<Rental> findByMemberId(Long memberId);

	/**
	 * Admin에서 체험 목록 조회시 사용
	 */
	@Query(
		"SELECT r FROM Rental r JOIN r.member m "
			+ "WHERE (:orderNumber IS NULL OR r.rentalNumber LIKE CONCAT('%', :orderNumber, '%')) "
			+ "AND (:providerId IS NULL OR m.providerId LIKE CONCAT('%', :providerId, '%')) "
			+ "AND (:startDate IS NULL OR r.detail.createdAt >= :startDate) "
			+ "AND (:endDate IS NULL OR r.detail.createdAt < :endDate)"
	)
	Page<Rental> search(
		@Param("orderNumber") String orderNumber,
		@Param("providerId") String providerId,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate,
		Pageable pageable
	);
}
