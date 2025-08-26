package ok.cherry.auth.jwt;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ok.cherry.auth.exception.TokenError;
import ok.cherry.global.exception.error.BusinessException;

@SpringBootTest
class TokenGeneratorTest {

	private static final String TEST_SECRET_KEY = "crailotestjwtsecretkey2025fordevelopmentandtestingonlylonglonglonglonglonglonglonglonglong==";

	@Autowired
	private TokenValidator tokenValidator;

	@Autowired
	private TokenGenerator tokenGenerator;

	private SecretKey testKey;

	@BeforeEach
	void setUp() {
		byte[] keyBytes = Decoders.BASE64.decode(TEST_SECRET_KEY);
		testKey = Keys.hmacShaKeyFor(keyBytes);
		tokenValidator = new TokenValidator(TEST_SECRET_KEY);
		TokenExtractor tokenExtractor = new TokenExtractor(TEST_SECRET_KEY);
		tokenGenerator = new TokenGenerator(TEST_SECRET_KEY, 10, 10, tokenValidator, tokenExtractor);
	}

	@Test
	@DisplayName("Authentication에 있는 회원 정보로 토큰을 발급한다.")
	void generateTokens() {
		// given
		String providerId = "12345";

		// when
		var result = tokenGenerator.generateTokenDTO(providerId);

		// then
		assertThat(result.tokenType()).isEqualTo("Bearer");
		tokenValidator.validateToken(result.accessToken());
		tokenValidator.validateToken(result.refreshToken());
	}

	@Test
	@DisplayName("유효한 refreshToken 으로 accessToken을 재발급한다.")
	void refreshAccessToken() {
		// given
		String providerId = "12345";
		var tokenResponse = tokenGenerator.generateTokenDTO(providerId);

		// when
		var result = tokenGenerator.reissueAccessToken(tokenResponse.refreshToken());

		// then
		tokenValidator.validateToken(result.accessToken());
	}

	@Test
	@DisplayName("만료된 refreshToken 으로 accessToken 재발급을 시도하면 예외가 발생한다.")
	void shouldThrowExceptionWhenReissuingAccessTokenWithInvalidRefreshToken() {
		// given
		String providerId = "12345";
		String invalidRefreshToken = Jwts.builder()
			.setSubject(providerId)
			.claim("isRefreshToken", true)
			.setExpiration(new Date(System.currentTimeMillis() - 1000))
			.signWith(testKey, SignatureAlgorithm.HS512)
			.compact();

		// when & then
		assertThatThrownBy(() -> tokenGenerator.reissueAccessToken(invalidRefreshToken))
			.isInstanceOf(BusinessException.class)
			.hasMessage(TokenError.INVALID_REFRESH_TOKEN.getMessage());
	}

	@Test
	@DisplayName("isRefreshToken 클레임이 없는 refreshToken 으로 accessToken 재발급을 시도하면 예외가 발생한다.")
	void shouldThrowExceptionWhenReissuingAccessTokenWithoutRefreshTokenClaim() {
		// given
		String providerId = "12345";
		String tokenWithoutClaim = Jwts.builder()
			.setSubject(providerId)
			.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60))
			.signWith(testKey, SignatureAlgorithm.HS512)
			.compact();

		// when & then
		assertThatThrownBy(() -> tokenGenerator.reissueAccessToken(tokenWithoutClaim))
			.isInstanceOf(BusinessException.class)
			.hasMessage(TokenError.INVALID_REFRESH_TOKEN.getMessage());
	}

	@Test
	@DisplayName("isRefreshToken 클레임이 false인 refreshToken 으로 accessToken 재발급을 시도하면 예외가 발생한다.")
	void shouldThrowExceptionWhenReissuingAccessTokenWithFalseRefreshTokenClaim() {
		// given
		String providerId = "12345";
		String tokenWithFalseClaim = Jwts.builder()
			.setSubject(providerId)
			.claim("isRefreshToken", false)
			.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60))
			.signWith(testKey, SignatureAlgorithm.HS512)
			.compact();

		// when & then
		assertThatThrownBy(() -> tokenGenerator.reissueAccessToken(tokenWithFalseClaim))
			.isInstanceOf(BusinessException.class)
			.hasMessage(TokenError.INVALID_REFRESH_TOKEN.getMessage());
	}
}
