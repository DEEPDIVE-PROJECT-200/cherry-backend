package ok.cherry.member.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ok.cherry.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByProviderId(String providerId);

	boolean existsByProviderId(String providerId);

	boolean existsByEmailAddress(String emailAddress);

	boolean existsByNickname(String nickname);
}
