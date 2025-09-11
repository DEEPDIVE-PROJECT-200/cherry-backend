package ok.cherry.member.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import ok.cherry.auth.application.AuthService;
import ok.cherry.auth.application.dto.response.TokenResponse;
import ok.cherry.config.EmbeddedRedisTestConfiguration;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.global.redis.AuthRedisRepository;
import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.member.domain.MemberStatus;
import ok.cherry.member.exception.MemberError;
import ok.cherry.member.infrastructure.MemberRepository;

@SpringBootTest
@Transactional
@Import(EmbeddedRedisTestConfiguration.class)
class MemberApplicationServiceTest {

	private static final DateTimeFormatter DEACTIVATION_DATE_FORMATTER = DateTimeFormatter.ofPattern("_yyyyMMddHHmmss");

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private AuthRedisRepository authRedisRepository;

	@Autowired
	private MemberApplicationService memberApplicationService;

	@Autowired
	private AuthService authService;

	@Test
	@DisplayName("회원 삭제에 성공한다")
	void deactivateMember_success() {
		// given
		Member savedMember = memberRepository.save(MemberBuilder.create());

		String originalEmail = savedMember.getEmail().address();
		String originalNickname = savedMember.getNickname();
		String originalProviderId = savedMember.getProviderId();

		TokenResponse tokenResponse = authService.login(savedMember.getProviderId());
		String accessToken = tokenResponse.accessToken();

		// when
		memberApplicationService.deactivateMember(accessToken, savedMember.getProviderId());

		// then
		Member deactivatedMember = memberRepository.findByProviderIdWithDeactivateMember(savedMember.getProviderId())
			.orElseThrow();
		assertThat(deactivatedMember.getMemberStatus()).isEqualTo(MemberStatus.DEACTIVATED);

		// 이메일, 닉네임, providerId에 탈퇴일시가 포맷되어 저장되었는지 검증
		LocalDateTime deactivatedAt = deactivatedMember.getDetail().getDeactivatedAt();
		String formattedTime = deactivatedAt.format(DEACTIVATION_DATE_FORMATTER);

		int atIndex = originalEmail.indexOf('@');
		String localPart = originalEmail.substring(0, atIndex);
		String domainPart = originalEmail.substring(atIndex);

		assertThat(deactivatedMember.getEmail().address()).isEqualTo(localPart + formattedTime + domainPart);
		assertThat(deactivatedMember.getNickname()).isEqualTo(originalNickname + formattedTime);
		assertThat(deactivatedMember.getProviderId()).isEqualTo(originalProviderId + formattedTime);

		// 로그아웃 되었는지 검증
		assertThat(authRedisRepository.getRefreshToken(savedMember.getProviderId())).isNull();
	}

	@Test
	@DisplayName("회원 조회 시 탈퇴한 회원은 제외하고 조회한다")
	void deactivateMember_success_exclude_deactivateMember() {
		// given
		Member activeMember = memberRepository.save(MemberBuilder.create());
		Member deactivateMember = memberRepository.save(
			MemberBuilder.builder()
				.withProviderId("67890")
				.withEmail("test123@test.com")
				.withNickname("test123")
				.build()
		);

		TokenResponse tokenResponse = authService.login(activeMember.getProviderId());
		String accessToken = tokenResponse.accessToken();

		// when
		memberApplicationService.deactivateMember(accessToken, deactivateMember.getProviderId());

		// then
		List<Member> foundMembers = memberRepository.findAll();
		assertThat(foundMembers).hasSize(1);
		assertThat(foundMembers.getFirst()).isEqualTo(activeMember);
	}

	@Test
	@DisplayName("삭제하려는 회원이 존재하지 않으면 예외가 발생한다")
	void deactivateMember_fail_userNotFound() {
		// given
		String nonExistentProviderId = "nonExistentProviderId";
		Member savedMember = memberRepository.save(MemberBuilder.create());
		TokenResponse tokenResponse = authService.login(savedMember.getProviderId());
		String accessToken = tokenResponse.accessToken();

		// when & then
		assertThatThrownBy(() -> memberApplicationService.deactivateMember(accessToken, nonExistentProviderId))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberError.USER_NOT_FOUND.getMessage());
	}
}