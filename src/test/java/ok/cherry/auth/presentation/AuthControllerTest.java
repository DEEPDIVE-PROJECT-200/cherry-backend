package ok.cherry.auth.presentation;

import static org.assertj.core.api.Assertions.*;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import ok.cherry.auth.application.dto.request.SignUpRequest;
import ok.cherry.auth.application.dto.response.ReissueTokenResponse;
import ok.cherry.auth.application.dto.response.TokenResponse;
import ok.cherry.auth.jwt.TokenGenerator;
import ok.cherry.config.EmbeddedRedisTestConfiguration;
import ok.cherry.global.redis.AuthRedisRepository;
import ok.cherry.member.domain.Member;
import ok.cherry.member.domain.Provider;
import ok.cherry.member.infrastructure.MemberRepository;

@SpringBootTest
@Import(EmbeddedRedisTestConfiguration.class)
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

	@Autowired
	MockMvcTester mvcTester;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	MemberRepository memberRepository;

	@Autowired
	TokenGenerator tokenGenerator;

	@Autowired
	AuthRedisRepository authRedisRepository;

	@Test
	@DisplayName("유효한 임시 토큰으로 회원가입 시 JWT 토큰을 반환한다")
	void signUpWithValidTempToken() throws JsonProcessingException, UnsupportedEncodingException {
		// given
		String providerId = "12345";
		String tempToken = tokenGenerator.generateTemporaryToken(providerId);
		
		SignUpRequest request = new SignUpRequest(tempToken, "test@example.com", "tester");
		String requestJson = objectMapper.writeValueAsString(request);

		// when
		MvcTestResult result = mvcTester.post()
			.uri("/api/v1/auth/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson)
			.exchange();

		// then
		assertThat(result).hasStatusOk()
			.bodyJson()
			.hasPathSatisfying("$.tokenType", value -> assertThat(value).isEqualTo("Bearer"))
			.hasPathSatisfying("$.accessToken", value -> assertThat(value).isNotNull())
			.hasPathSatisfying("$.refreshToken", value -> assertThat(value).isNotNull());

		TokenResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), TokenResponse.class);
		
		Member member = memberRepository.findByProviderId(providerId).orElseThrow();
		assertThat(member.getEmail().address()).isEqualTo("test@example.com");
		assertThat(member.getNickname()).isEqualTo("tester");
		assertThat(member.getProvider()).isEqualTo(Provider.KAKAO);
		
		String storedRefreshToken = authRedisRepository.getRefreshToken(providerId);
		assertThat(storedRefreshToken).isEqualTo(response.refreshToken());
	}

	@Test
	@DisplayName("유효하지 않은 임시 토큰으로 회원가입 시 401 오류를 반환한다")
	void signUpWithInvalidTempToken() throws JsonProcessingException {
		// given
		SignUpRequest request = new SignUpRequest("invalid_temp_token", "test@example.com", "tester");
		String requestJson = objectMapper.writeValueAsString(request);

		// when
		MvcTestResult result = mvcTester.post()
			.uri("/api/v1/auth/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson)
			.exchange();

		// then
		assertThat(result).hasStatus(401);
		assertThat(memberRepository.findAll()).isEmpty();
	}

	@Test
	@DisplayName("유효하지 않은 이메일로 회원가입 시 400 오류를 반환한다")
	void signUpWithInvalidEmail() throws JsonProcessingException {
		String providerId = "12345";
		String tempToken = tokenGenerator.generateTemporaryToken(providerId);
		
		SignUpRequest request = new SignUpRequest(tempToken, "invalid-email", "tester");
		String requestJson = objectMapper.writeValueAsString(request);

		// when
		MvcTestResult result = mvcTester.post()
			.uri("/api/v1/auth/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson)
			.exchange();

		// then
		assertThat(result).hasStatus(400);
		assertThat(memberRepository.findAll()).isEmpty();
	}

	@Test
	@DisplayName("중복 이메일로 회원가입 시 409 오류를 반환한다")
	void signUpWithDuplicateEmail() throws JsonProcessingException {
		// given
		String existingProviderId = "existing_kakao_123";
		Member existingMember = Member.register(existingProviderId, Provider.KAKAO, "test@example.com", "old");
		memberRepository.save(existingMember);
		
		String newProviderId = "new_kakao_456";
		String tempToken = tokenGenerator.generateTemporaryToken(newProviderId);
		
		SignUpRequest request = new SignUpRequest(tempToken, "test@example.com", "new");
		String requestJson = objectMapper.writeValueAsString(request);

		// when
		MvcTestResult result = mvcTester.post()
			.uri("/api/v1/auth/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson)
			.exchange();

		// then
		assertThat(result).hasStatus(409);
		assertThat(memberRepository.findAll()).hasSize(1);
		assertThat(memberRepository.findByProviderId(existingProviderId)).isPresent();
		assertThat(memberRepository.findByProviderId(newProviderId)).isEmpty();
	}

	@Test
	@DisplayName("유효한 리프레시 토큰으로 액세스 토큰 재발급을 성공한다")
	void reissueWithValidRefreshToken() throws JsonProcessingException, UnsupportedEncodingException {
		// given
		String providerId = "12345";
		String tempToken = tokenGenerator.generateTemporaryToken(providerId);
		
		SignUpRequest signUpRequest = new SignUpRequest(tempToken, "test@example.com", "tester");
		String signUpJson = objectMapper.writeValueAsString(signUpRequest);

		MvcTestResult signUpResult = mvcTester.post()
			.uri("/api/v1/auth/signup")
			.contentType(MediaType.APPLICATION_JSON)
			.content(signUpJson)
			.exchange();

		TokenResponse originalTokenResponse = objectMapper.readValue(
			signUpResult.getResponse().getContentAsString(), TokenResponse.class);

		// when
		MvcTestResult reissueResult = mvcTester.post()
			.uri("/api/v1/auth/reissue")
			.cookie(new Cookie("refreshToken", originalTokenResponse.refreshToken()))
			.exchange();

		// then
		assertThat(reissueResult).hasStatusOk()
			.bodyJson()
			.hasPathSatisfying("$.tokenType", value -> assertThat(value).isEqualTo("Bearer"))
			.hasPathSatisfying("$.accessToken", value -> assertThat(value).isNotNull())
			.hasPathSatisfying("$.accessTokenExpiresInSeconds", value -> assertThat(value).isNotNull());

		ReissueTokenResponse reissueResponse = objectMapper.readValue(
			reissueResult.getResponse().getContentAsString(), ReissueTokenResponse.class);

		assertThat(reissueResponse.accessToken()).isNotNull();
		assertThat(reissueResponse.tokenType()).isEqualTo("Bearer");
	}

	@Test
	@DisplayName("리프레시 토큰이 없으면 401 오류를 반환한다")
	void reissueWithoutRefreshToken() {
		// given
		// refreshToken 존재 X

		// when
		MvcTestResult result = mvcTester.post()
			.uri("/api/v1/auth/reissue")
			.exchange();

		// then
		assertThat(result).hasStatus(401);
	}

	@Test
	@DisplayName("유효하지 않은 리프레시 토큰으로 재발급 시 401 오류를 반환한다")
	void reissueWithInvalidRefreshToken() {
		//given
		String refreshToken = "invalid_refresh_token";

		// when
		MvcTestResult result = mvcTester.post()
			.uri("/api/v1/auth/reissue")
			.cookie(new Cookie("refreshToken", refreshToken))
			.exchange();

		// then
		assertThat(result).hasStatus(401);
	}
}