package ok.cherry.auth.jwt;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ok.cherry.auth.application.dto.response.ReissueTokenResponse;
import ok.cherry.auth.application.dto.response.TokenResponse;
import ok.cherry.auth.exception.TokenError;
import ok.cherry.global.exception.error.BusinessException;

@Component
public class TokenGenerator {

	private static final String AUTHORITIES_KEY = "auth";
	private static final String BEARER_TYPE = "Bearer";

	private final Duration accessTokenExpiration;
	private final Duration refreshTokenExpiration;
	private final Duration tempTokenExpiration;
	private final TokenValidator tokenValidator;
	private final TokenExtractor tokenExtractor;
	private final Key key;

	public TokenGenerator(
		@Value("${jwt.secret}") String secretKey,
		@Value("${jwt.access-token-expiration-seconds}") long accessTokenExpirationSeconds,
		@Value("${jwt.refresh-token-expiration-seconds}") long refreshTokenExpirationSeconds,
		@Value("${jwt.temp-token-expiration-seconds}") long tempTokenExpirationSeconds,
		TokenValidator tokenValidator,
		TokenExtractor tokenExtractor
	) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.accessTokenExpiration = Duration.ofSeconds(accessTokenExpirationSeconds);
		this.refreshTokenExpiration = Duration.ofSeconds(refreshTokenExpirationSeconds);
		this.tempTokenExpiration = Duration.ofSeconds(tempTokenExpirationSeconds);
		this.tokenValidator = tokenValidator;
		this.tokenExtractor = tokenExtractor;
	}

	/**
	 * 토큰 생성 메서드
	 * */
	public TokenResponse generateTokenDTO(String providerId) {
		String accessToken = generateAccessToken(providerId);
		String refreshToken = generateRefreshToken(providerId);

		long now = System.currentTimeMillis();
		Date accessTokenExpiresIn = new Date(now + accessTokenExpiration.toMillis());

		return new TokenResponse(
			BEARER_TYPE,
			accessToken,
			accessTokenExpiresIn.getTime(),
			refreshToken
		);
	}

	/**
	 * AccessToken 재발급
	 * */
	public ReissueTokenResponse reissueAccessToken(String refreshToken) {
		// 리프레시 토큰에서 사용자 정보 추출 -> 클레임 확인
		Claims claims = tokenExtractor.parseClaims(refreshToken);

		// Refresh Token 검증 및 클레임에서 Refresh Token 여부 확인
		if (!tokenValidator.validateToken(refreshToken) || claims.get("isRefreshToken") == null || !Boolean.TRUE.equals(
			claims.get("isRefreshToken"))) {
			throw new BusinessException(TokenError.INVALID_REFRESH_TOKEN);
		}

		String providerId = claims.getSubject();
		String newAccessToken = generateAccessToken(providerId);
		long now = System.currentTimeMillis();
		Date accessTokenExpiresIn = new Date(now + accessTokenExpiration.toMillis());

		return new ReissueTokenResponse(
			BEARER_TYPE,
			newAccessToken,
			accessTokenExpiresIn.getTime()
		);
	}

	/**
	 * TempToken 생성
	 * */
	public String generateTempToken(String providerId) {
		long now = System.currentTimeMillis();
		Date temporaryTokenExpiresIn = new Date(now + tempTokenExpiration.toMillis());
		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setSubject(providerId)
			.claim(AUTHORITIES_KEY, "TEMPORARY_TOKEN")
			.setExpiration(temporaryTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();
	}

	/**
	 * AccessToken 생성
	 * */
	private String generateAccessToken(String providerId) {
		long now = System.currentTimeMillis();
		Date accessTokenExpiresIn = new Date(now + accessTokenExpiration.toMillis());
		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setSubject(providerId)
			.setExpiration(accessTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();
	}

	/**
	 * RefreshToken 생성
	 * */
	private String generateRefreshToken(String providerId) {
		long now = System.currentTimeMillis();
		Date refreshTokenExpiresIn = new Date(now + refreshTokenExpiration.toMillis());
		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setSubject(providerId)
			.setExpiration(refreshTokenExpiresIn)
			.claim("isRefreshToken", true) // refreshToken 임을 나타내는 클레임 추가
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();
	}
}
