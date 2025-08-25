package ok.cherry.auth.jwt;

import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class TokenExtractor {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer ";

	private final Key key;

	public TokenExtractor(@Value("${jwt.secret}") String secretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * 토큰 추출
	 * */
	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(BEARER_PREFIX.length());
		}

		return null;
	}

	/**
	 * providerId 추출
	 * */
	public String getProviderId(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	/**
	 * AccessToken 유효시간 추출
	 * */
	public Duration getAccessTokenExpiration(String accessToken) {
		Claims claims = parseClaims(accessToken);
		Date expiration = claims.getExpiration();

		// 현재시간 기준으로 남은 유효시간 계산
		return Duration.ofMillis(expiration.getTime() - System.currentTimeMillis());
	}

	/**
	 * 권한 추출
	 **/
	public Authentication getAuthentication(String token) {
		String providerId = getProviderId(token);
		return new UsernamePasswordAuthenticationToken(providerId, null, Collections.emptyList());
	}

	/**
	 * 클레임 추출
	 * */
	public Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			// 토큰이 만료되어 예외가 발생하더라도 클레임 값들은 뽑을 수 있음
			return e.getClaims();
		}
	}
}
