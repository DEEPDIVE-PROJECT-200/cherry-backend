package ok.cherry.auth.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.MalformedJwtException;
import ok.cherry.auth.application.dto.response.ReissueTokenResponse;
import ok.cherry.auth.application.dto.response.SignUpResponse;
import ok.cherry.auth.application.dto.response.TokenResponse;
import ok.cherry.auth.jwt.TokenGenerator;
import ok.cherry.config.EmbeddedRedisTestConfiguration;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.global.redis.AuthRedisRepository;
import ok.cherry.member.domain.Member;
import ok.cherry.member.domain.Provider;
import ok.cherry.member.exception.MemberError;
import ok.cherry.member.infrastructure.MemberRepository;

@SpringBootTest
@Import(EmbeddedRedisTestConfiguration.class)
@Transactional
class AuthServiceTest {

	@Autowired
	AuthService authService;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	AuthRedisRepository authRedisRepository;

	@Autowired
	TokenGenerator tokenGenerator;

	@Test
	@DisplayName("신규 사용자가 회원가입에 성공한다")
	void signUp() {
		// given
		String providerId = "1";
		String tempToken = tokenGenerator.generateTemporaryToken(providerId);
		String emailAddress = "test@example.com";
		String nickname = "tester";

		// when
		SignUpResponse response = authService.signUp(emailAddress, nickname, tempToken);

		// then
		assertThat(response.providerId()).isEqualTo(providerId);
		Member savedMember = memberRepository.findByProviderId(providerId).orElseThrow();

		assertThat(savedMember.getEmail().address()).isEqualTo(emailAddress);
		assertThat(savedMember.getNickname()).isEqualTo(nickname);
		assertThat(savedMember.getProvider()).isEqualTo(Provider.KAKAO);
	}

	@Test
	@DisplayName("이미 등록된 사용자가 회원가입 시 실패한다")
	void signUpWithAlreadyRegisteredUser() {
		// given
		String providerId = "1";
		String tempToken = tokenGenerator.generateTemporaryToken(providerId);
		Member existingMember = Member.register(providerId, Provider.KAKAO, "existing@example.com", "existing");
		memberRepository.save(existingMember);

		// when & then
		assertThatThrownBy(() -> authService.signUp(providerId, "new@example.com", tempToken))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberError.ALREADY_REGISTERED.getMessage());
	}

	@Test
	@DisplayName("중복 이메일로 회원가입 시 실패한다")
	void signUpWithDuplicateEmail() {
		// given
		String duplicateEmail = "duplicate@example.com";
		Member existingMember = Member.register("1", Provider.KAKAO, duplicateEmail, "existing");
		memberRepository.save(existingMember);
		String tempToken = tokenGenerator.generateTemporaryToken("2");

		// when & then
		assertThatThrownBy(() -> authService.signUp(duplicateEmail, "new", tempToken))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberError.DUPLICATE_EMAIL.getMessage());
	}

	@Test
	@DisplayName("중복 닉네임으로 회원가입 시 실패한다")
	void signUpWithDuplicateNickname() {
		// given
		String duplicateNickname = "duplicate";
		Member existingMember = Member.register("1", Provider.KAKAO, "existing@example.com", duplicateNickname);
		memberRepository.save(existingMember);
		String tempToken = tokenGenerator.generateTemporaryToken("2");

		// when & then
		assertThatThrownBy(() -> authService.signUp("new@example.com", duplicateNickname, tempToken))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberError.DUPLICATE_NICKNAME.getMessage());
	}

	@Test
	@DisplayName("등록된 사용자가 로그인에 성공한다")
	void login() {
		// given
		String providerId = "1";
		Member member = Member.register("1", Provider.KAKAO, "test@example.com", "tester");
		memberRepository.save(member);

		// when
		TokenResponse response = authService.login(providerId);

		// then
		assertThat(response.tokenType()).isEqualTo("Bearer");
		assertThat(response.accessToken()).isNotNull();
		assertThat(response.refreshToken()).isNotNull();

		String storedRefreshToken = authRedisRepository.getRefreshToken(providerId);
		assertThat(storedRefreshToken).isEqualTo(response.refreshToken());
	}

	@Test
	@DisplayName("등록되지 않은 사용자가 로그인 시 실패한다")
	void loginWithUnregisteredUser() {
		// given
		String nonExistentProviderId = "none";

		// when & then
		assertThatThrownBy(() -> authService.login(nonExistentProviderId))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberError.USER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("로그아웃 시 리프레시 토큰을 삭제한다")
	void logout() {
		// given
		String providerId = "1";
		Member member = Member.register(providerId, Provider.KAKAO, "test@example.com", "tester");
		memberRepository.save(member);

		TokenResponse tokenResponse = authService.login(providerId);
		String accessToken = tokenResponse.accessToken();

		assertThat(authRedisRepository.getRefreshToken(providerId)).isNotNull();

		// when
		authService.logout(accessToken, providerId);

		// then
		assertThat(authRedisRepository.getRefreshToken(providerId)).isNull();
	}

	@Test
	@DisplayName("유효한 리프레시 토큰으로 액세스 토큰 재발급에 성공한다")
	void reissue() {
		// given
		String providerId = "kakao_12345";
		Member member = Member.register(providerId, Provider.KAKAO, "test@example.com", "tester");
		memberRepository.save(member);

		TokenResponse originalTokenResponse = authService.login(providerId);

		// when
		ReissueTokenResponse reissueResponse = authService.reissueAccessToken(originalTokenResponse.refreshToken());

		// then
		assertThat(reissueResponse.tokenType()).isEqualTo("Bearer");
		assertThat(reissueResponse.accessToken()).isNotNull();
		assertThat(reissueResponse.accessTokenExpiresInSeconds()).isNotNull();
	}

	@Test
	@DisplayName("잘못된 리프레시 토큰으로 재발급 시 실패한다")
	void reissueWithInvalidRefreshToken() {
		// given
		String providerId = "1";
		Member member = Member.register(providerId, Provider.KAKAO, "test@example.com", "tester");
		memberRepository.save(member);
		String wrongRefreshToken = "wrong_refresh_token";

		// when & then
		assertThatThrownBy(() -> authService.reissueAccessToken(wrongRefreshToken))
			.isInstanceOf(MalformedJwtException.class);
	}
}