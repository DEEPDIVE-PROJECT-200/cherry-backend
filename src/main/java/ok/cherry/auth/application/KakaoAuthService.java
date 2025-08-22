package ok.cherry.auth.application;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.auth.application.dto.response.CheckMemberResponse;
import ok.cherry.auth.application.dto.response.KakaoTokenResponse;
import ok.cherry.auth.application.dto.response.KakaoIdResponse;
import ok.cherry.auth.exception.AuthError;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.infrastructure.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

	private final RestTemplate restTemplate; // 카카오 API와 HTTP 통신을 위해 사용
	private final MemberRepository memberRepository;

	@Value("${kakao.client_id}")
	private String clientId;

	@Value("${kakao.redirect_uri}")
	private String redirectUri;

	public String getAccessToken(String code) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		String tokenUrl = "https://kauth.kakao.com/oauth/token";
		KakaoTokenResponse response = Optional.ofNullable(restTemplate.exchange(
			tokenUrl, HttpMethod.POST, request, KakaoTokenResponse.class)
			.getBody())
			.orElseThrow(() -> new BusinessException(AuthError.KAKAO_TOKEN_RESPONSE_EMPTY));

		log.info("accessToken: {}", response.accessToken());
		log.info("refreshToken: {}", response.refreshToken());

		return response.accessToken();
	}

	public String getProviderId(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);

		HttpEntity<Void> request = new HttpEntity<>(headers);

		String url = "https://kapi.kakao.com/v2/user/me";
		KakaoIdResponse response = Optional.ofNullable(restTemplate.exchange(
			url, HttpMethod.GET, request, KakaoIdResponse.class)
			.getBody())
			.orElseThrow(() -> new BusinessException(AuthError.KAKAO_PROVIDER_ID_EMPTY));

		log.info("providerId: {}", response.providerId());
		return response.providerId();
	}

	public CheckMemberResponse checkMember(String providerId) {
		boolean isMember = memberRepository.existsByProviderId(providerId);
		return new CheckMemberResponse(providerId, isMember);
	}


}
