package ok.cherry.payment.infrastructure;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ok.cherry.payment.domain.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	boolean existsByRentalId(Long rentalId);

	List<Payment> findByMemberProviderIdOrderByDetailCreatedAtDesc(String providerId);

	Optional<Payment> findByRentalId(Long rentalId);

	@SuppressWarnings("checkstyle:OperatorWrap")
	@Query("SELECT p FROM Payment p WHERE p.member.providerId = :providerId "
		+ "AND p.detail.createdAt >= :fromDate "
		+ "ORDER BY p.detail.createdAt DESC")
	List<Payment> findRecentPaymentsByProviderId(
		@Param("providerId") String providerId,
		@Param("fromDate") LocalDateTime fromDate
	);
}
