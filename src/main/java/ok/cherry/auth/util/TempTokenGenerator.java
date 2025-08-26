package ok.cherry.auth.util;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ok.cherry.global.redis.AuthRedisRepository;

@Component
@RequiredArgsConstructor
public class TempTokenGenerator {

	private static final int TOKEN_LENGTH = 32;
	private static final Duration TEMP_TOKEN_EXPIRATION = Duration.ofMinutes(10);

	private final SecureRandom secureRandom = new SecureRandom();
	private final AuthRedisRepository authRedisRepository;

	public String generateTempToken(String providerId) {
		byte[] tokenBytes = new byte[TOKEN_LENGTH];
		secureRandom.nextBytes(tokenBytes);
		String tempToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
		
		// Redis에 임시 토큰과 providerId 매핑 저장 (10분 만료)
		authRedisRepository.saveTempToken(tempToken, providerId, TEMP_TOKEN_EXPIRATION);
		
		return tempToken;
	}

	public String validateAndRemoveTempToken(String tempToken) {
		if (tempToken == null || tempToken.isEmpty()) {
			return null;
		}
		
		String providerId = authRedisRepository.getTempToken(tempToken);
		if (providerId != null) {
			// 일회성 사용을 위해 사용 즉시 삭제
			authRedisRepository.deleteTempToken(tempToken);
			return providerId;
		}
		
		return null;
	}
}