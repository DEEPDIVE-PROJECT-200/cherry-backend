package ok.cherry.auth.util;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ok.cherry.global.redis.AuthRedisRepository;

@Component
@RequiredArgsConstructor
public class StateGenerator {

	private static final int STATE_LENGTH = 32;
	private static final Duration STATE_EXPIRATION = Duration.ofMinutes(10);

	private final SecureRandom secureRandom = new SecureRandom();
	private final AuthRedisRepository authRedisRepository;

	public String generateState() {
		byte[] stateBytes = new byte[STATE_LENGTH];
		secureRandom.nextBytes(stateBytes);
		String state = Base64.getUrlEncoder().withoutPadding().encodeToString(stateBytes);
		
		// Redis에 state 저장 (10분 만료)
		authRedisRepository.saveOAuthState(state, "valid", STATE_EXPIRATION);
		
		return state;
	}

	public boolean validateAndRemoveState(String state) {
		if (state == null || state.isEmpty()) {
			return false;
		}
		
		String storedState = authRedisRepository.getOAuthState(state);
		if (storedState != null) {
			// 일회성 사용을 위해 사용 즉시 삭제
			authRedisRepository.deleteOAuthState(state);
			return true;
		}
		
		return false;
	}
}