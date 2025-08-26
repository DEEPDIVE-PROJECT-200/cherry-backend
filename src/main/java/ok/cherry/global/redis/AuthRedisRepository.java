package ok.cherry.global.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuthRedisRepository {

	private final RedisTemplate<String, String> stringRedisTemplate;
	private final RedisTemplate<String, Object> objectRedisTemplate;
	private final RedisKeyGenerator redisKeyGenerator;
	private final Duration refreshTokenExpiration;

	public AuthRedisRepository(
		RedisTemplate<String, String> stringRedisTemplate,
		RedisTemplate<String, Object> objectRedisTemplate,
		RedisKeyGenerator redisKeyGenerator,
		@Value("${jwt.refresh-token-expiration-seconds}") long refreshTokenExpirationSeconds
	) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.objectRedisTemplate = objectRedisTemplate;
		this.redisKeyGenerator = redisKeyGenerator;
		this.refreshTokenExpiration = Duration.ofSeconds(refreshTokenExpirationSeconds);
	}

	/**
	 * RefreshToken 저장
	 * Key = auth:refreshToken:{providerId}
	 * */
	public void saveRefreshToken(String providerId, String refreshToken) {
		String key = redisKeyGenerator.generateRefreshTokenKey(providerId);
		stringRedisTemplate.opsForValue()
			.set(key, refreshToken, refreshTokenExpiration);
	}

	/**
	 * Logout 관련
	 * Key = auth:accessToken:logout:{token}
	 * */
	public void saveLogoutToken(String accessToken, LogoutToken logoutToken, Duration expireTime) {
		if (expireTime == null || expireTime.isZero() || expireTime.isNegative()) {
			return; // 이미 만료 또는 유효하지 않은 TTL은 저장하지 않음
		}
		String key = redisKeyGenerator.generateLogoutTokenKey(accessToken);
		objectRedisTemplate.opsForValue().set(key, logoutToken, expireTime);
	}

	public String getRefreshToken(String providerId) {
		String key = redisKeyGenerator.generateRefreshTokenKey(providerId);
		return stringRedisTemplate.opsForValue().get(key);
	}

	public void deleteRefreshToken(String providerId) {
		String key = redisKeyGenerator.generateRefreshTokenKey(providerId);
		stringRedisTemplate.delete(key);
	}

	public LogoutToken getLogoutToken(String accessToken) {
		String key = redisKeyGenerator.generateLogoutTokenKey(accessToken);
		Object value = objectRedisTemplate.opsForValue().get(key);
		return (LogoutToken)value;
	}
}
