package ok.cherry.auth.presentation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import ok.cherry.auth.application.dto.response.KakaoIdResponse;
import ok.cherry.auth.application.dto.response.KakaoTokenResponse;
import ok.cherry.auth.util.TempTokenGenerator;
import ok.cherry.config.EmbeddedRedisTestConfiguration;
import ok.cherry.member.domain.Member;
import ok.cherry.member.domain.Provider;
import ok.cherry.member.infrastructure.MemberRepository;

@SpringBootTest
@Import(EmbeddedRedisTestConfiguration.class)
@AutoConfigureMockMvc
@Transactional
class KakaoLoginControllerTest {

	@Autowired
	MockMvcTester mvcTester;

	@Autowired
	MemberRepository memberRepository;

	@MockitoBean
	RestTemplate restTemplate;
	
	@MockitoBean
	TempTokenGenerator tempTokenGenerator;

	@Test
	@DisplayName("기존 회원이 카카오 로그인 콜백 시 JWT 토큰을 반환한다")
	void callbackWithExistingMember() {
		// given
		Member member = Member.register("12345", Provider.KAKAO, "test@test.com", "tester");
		memberRepository.save(member);

		KakaoTokenResponse tokenResponse = createKakaoTokenResponse();
		KakaoIdResponse idResponse = new KakaoIdResponse("12345");

		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
			eq(KakaoTokenResponse.class))).thenReturn(ResponseEntity.ok(tokenResponse));
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
			eq(KakaoIdResponse.class))).thenReturn(ResponseEntity.ok(idResponse));

		// when
		MvcTestResult result = mvcTester.get().uri("/auth/callback?code=test_code&state=valid_state").exchange();

		// then
		assertThat(result).hasStatusOk()
			.bodyJson()
			.hasPathSatisfying("$.tokenType", value -> assertThat(value).isEqualTo("Bearer"))
			.hasPathSatisfying("$.accessToken", value -> assertThat(value).isNotNull());
	}

	@Test
	@DisplayName("신규 사용자가 카카오 로그인 콜백 시 임시 토큰을 반환한다")
	void callbackWithNewUser() {
		// given
		KakaoTokenResponse tokenResponse = createKakaoTokenResponse();
		KakaoIdResponse idResponse = new KakaoIdResponse("9999");

		when(tempTokenGenerator.generateTempToken("9999")).thenReturn("temp_token_12345");
		when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
			eq(KakaoTokenResponse.class))).thenReturn(ResponseEntity.ok(tokenResponse));
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
			eq(KakaoIdResponse.class))).thenReturn(ResponseEntity.ok(idResponse));

		// when
		MvcTestResult result = mvcTester.get().uri("/auth/callback?code=test_code&state=valid_state").exchange();

		// then
		assertThat(result).hasStatus(202)  // Accepted
			.bodyJson()
			.hasPathSatisfying("$.tempToken", value -> assertThat(value).isEqualTo("temp_token_12345"));
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