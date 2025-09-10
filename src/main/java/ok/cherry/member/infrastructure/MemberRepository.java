package ok.cherry.member.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ok.cherry.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByProviderId(String providerId);

	boolean existsByProviderId(String providerId);

	boolean existsByEmailAddress(String emailAddress);

	boolean existsByNickname(String nickname);

	@Query(value = "SELECT * FROM member m WHERE m.provider_id = :providerId", nativeQuery = true)
	Optional<Member> findByProviderIdWithDeactivateMember(@Param("providerId") String providerId);
}
