package ok.cherry.auth.application;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityManager;
import ok.cherry.auth.application.dto.response.CheckMemberResponse;
import ok.cherry.auth.application.dto.response.KakaoIdResponse;
import ok.cherry.auth.application.dto.response.KakaoTokenResponse;
import ok.cherry.auth.exception.AuthError;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.member.domain.Member;
import ok.cherry.member.domain.Provider;
import ok.cherry.member.infrastructure.MemberRepository;

@SpringBootTest
@Transactional
class KakaoOAuthServiceTest {

	@Autowired
	KakaoOAuthService kakaoOAuthService;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	EntityManager entityManager;

	@MockitoBean
	RestTemplate restTemplate;

	@Test
	@DisplayName("kakao 로그인시 받은 코드로 AccessToken을 가져올 수 있다")
	void getAccessToken() {
		//given
		KakaoTokenResponse response = createKakaoTokenResponse();

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST),
			any(HttpEntity.class), eq(KakaoTokenResponse.class)))
			.thenReturn(ResponseEntity.ok(response));

		// when
		String accessToken = kakaoOAuthService.getAccessToken("test_code");

		// then
		assertThat(accessToken).isEqualTo("access_token");
	}

	@Test
	@DisplayName("카카오 토큰 응답이 null일 때 예외가 발생한다")
	void getAccessTokenFail() {
		// given
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST),
			any(HttpEntity.class), eq(KakaoTokenResponse.class)))
			.thenReturn(ResponseEntity.ok(null));

		// when & then
		assertThatThrownBy(() -> kakaoOAuthService.getAccessToken("test_code"))
			.isInstanceOf(BusinessException.class)
			.hasMessage(AuthError.KAKAO_TOKEN_RESPONSE_EMPTY.getMessage());
	}

	@Test
	@DisplayName("발급 받은 AccessToken으로 provideId를 가져올 수 있다")
	void getProviderId() {
		// given
		var response = new KakaoIdResponse("provider_id");

		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
			any(HttpEntity.class), eq(KakaoIdResponse.class)))
			.thenReturn(ResponseEntity.ok(response));

		// when
		String providerId = kakaoOAuthService.getProviderId("access_token");

		// then
		assertThat(providerId).isEqualTo("provider_id");
	}

	@Test
	@DisplayName("accessToken으로 가져온 providerId 응답이 null일 때 예외가 발생한다")
	void getProviderIdFail() {
		// given
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
			any(HttpEntity.class), eq(KakaoIdResponse.class)))
			.thenReturn(ResponseEntity.ok(null));

		// when & then
		assertThatThrownBy(() -> kakaoOAuthService.getProviderId("access_token"))
			.isInstanceOf(BusinessException.class)
			.hasMessage(AuthError.KAKAO_PROVIDER_ID_EMPTY.getMessage());
	}

	@Test
	@DisplayName("존재하는 providerId로 회원 조회 시 회원 정보를 반환한다")
	void checkMemberWithExistingProviderId() {
		// given
		Member member = Member.register("1", Provider.KAKAO, "test@test.com", "tester");
		memberRepository.save(member);
		entityManager.flush();
		entityManager.clear();

		// when
		CheckMemberResponse response = kakaoOAuthService.checkMember("1");

		// then
		assertThat(response.isMember()).isTrue();
		assertThat(response.providerId()).isEqualTo("1");
	}

	@Test
	@DisplayName("존재하지 않는 providerId로 회원 조회 시 회원이 아님을 반환한다")
	void checkMemberWithNonExistingProviderId() {
		// given
		// 별도의 회원 데이터 없음

		// when
		CheckMemberResponse response = kakaoOAuthService.checkMember("2");

		// then
		assertThat(response.isMember()).isFalse();
		assertThat(response.providerId()).isEqualTo("2");
	}

	private KakaoTokenResponse createKakaoTokenResponse() {
		return new KakaoTokenResponse(
			"bearer",
			"access_token",
			null,
			10000,
			"refresh_token",
			10000,
			"account_email profile_nickname"
		);
	}
}