package ok.cherry.global.redis;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyGenerator {

	/**
	 * Redis 키 Prefix
	 * */
	private static final String REFRESH_TOKEN_KEY_PREFIX = "auth:refreshToken:{providerId}";
	private static final String LOGOUT_KEY_PREFIX = "auth:accessToken:logout:{token}";

	public String generateRefreshTokenKey(String providerId) {
		return REFRESH_TOKEN_KEY_PREFIX.replace("{providerId}", providerId);
	}

	public String generateLogoutTokenKey(String accessToken) {
		return LOGOUT_KEY_PREFIX.replace("{token}", accessToken);
	}
}
