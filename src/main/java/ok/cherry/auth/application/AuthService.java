package ok.cherry.auth.application;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ok.cherry.auth.application.dto.request.SignUpRequest;
import ok.cherry.auth.application.dto.response.ReissueTokenResponse;
import ok.cherry.auth.application.dto.response.SignUpResponse;
import ok.cherry.auth.application.dto.response.TokenResponse;
import ok.cherry.auth.exception.TokenError;
import ok.cherry.auth.jwt.TokenExtractor;
import ok.cherry.auth.jwt.TokenGenerator;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.global.redis.AuthRedisRepository;
import ok.cherry.global.redis.LogoutToken;
import ok.cherry.member.domain.Member;
import ok.cherry.member.domain.Provider;
import ok.cherry.member.exception.MemberError;
import ok.cherry.member.infrastructure.MemberRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

	private final MemberRepository memberRepository;
	private final AuthRedisRepository authRedisRepository;
	private final TokenGenerator tokenGenerator;
	private final TokenExtractor tokenExtractor;

	public SignUpResponse signUp(SignUpRequest request) {
		validateAlreadyRegister(request.providerId());
		validateEmailAddress(request.emailAddress());
		validateNickname(request.nickname());

		Member member = Member.register(request.providerId(), Provider.KAKAO, request.emailAddress(), request.nickname());
		memberRepository.save(member);
		return new SignUpResponse(request.providerId());
	}

	public TokenResponse login(String providerId) {
		if (!memberRepository.existsByProviderId(providerId)) {
			throw new BusinessException(MemberError.USER_NOT_FOUND);
		}
		TokenResponse tokenResponse = tokenGenerator.generateTokenDTO(providerId);
		authRedisRepository.saveRefreshToken(providerId, tokenResponse.refreshToken());
		return tokenResponse;
	}

	public void logout(String accessToken, String providerId) {
		if (authRedisRepository.getRefreshToken(providerId) != null) {
			authRedisRepository.deleteRefreshToken(providerId);
		}

		// AccessToken 남은 유효시간 기반으로 블랙리스트 TTL 설정
		Duration expiration = tokenExtractor.getAccessTokenExpiration(accessToken);
		if (expiration.isZero()) {
			// 이미 만료된 토큰은 블랙리스트 저장 불필요
			return;
		}
		LogoutToken logoutToken = new LogoutToken("logout", expiration);
		authRedisRepository.saveLogoutToken(accessToken, logoutToken, expiration);
	}

	public ReissueTokenResponse reissueAccessToken(String refreshToken) {
		String providerId = tokenExtractor.parseClaims(refreshToken).getSubject();

		String restoredRefreshToken = authRedisRepository.getRefreshToken(providerId);

		if (!refreshToken.equals(restoredRefreshToken)) {
			throw new BusinessException(TokenError.NOT_EQUALS_REFRESH_TOKEN);
		}

		return tokenGenerator.reissueAccessToken(refreshToken);
	}

	private void validateAlreadyRegister(String providerId) {
		if (memberRepository.existsByProviderId(providerId)) {
			throw new BusinessException(MemberError.ALREADY_REGISTER);
		}
	}

	private void validateNickname(String nickname) {
		if (memberRepository.existsByNickname(nickname)) {
			throw new BusinessException(MemberError.DUPLICATE_NICKNAME);
		}
	}

	private void validateEmailAddress(String emailAddress) {
		if (memberRepository.existsByEmailAddress(emailAddress)) {
			throw new BusinessException(MemberError.DUPLICATE_EMAIL);
		}
	}
}
