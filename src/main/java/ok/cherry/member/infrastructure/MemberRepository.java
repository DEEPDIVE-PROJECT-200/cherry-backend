package ok.cherry.member.infrastructure;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ok.cherry.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	@Query("""
		select m
		from Member m
		where (:nickname is null or m.nickname like concat('%', :nickname, '%'))
		  and (:emailAddress is null or m.email.address like concat('%', :emailAddress, '%'))
		""")
	Page<Member> search(@Param("nickname") String nickname, @Param("emailAddress") String emailAddress, Pageable pageable);

	Optional<Member> findByProviderId(String providerId);

	boolean existsByProviderId(String providerId);

	boolean existsByEmailAddress(String emailAddress);

	boolean existsByNickname(String nickname);

	@Query(value = "SELECT * FROM member m WHERE m.provider_id = :providerId", nativeQuery = true)
	Optional<Member> findByProviderIdWithDeactivateMember(@Param("providerId") String providerId);
}
