package ok.cherry.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import ok.cherry.auth.jwt.JwtAuthenticationEntryPoint;
import ok.cherry.auth.jwt.JwtFilter;
import ok.cherry.auth.jwt.TokenExtractor;
import ok.cherry.auth.jwt.TokenValidator;
import ok.cherry.global.redis.AuthRedisRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private static final String[] PERMIT_URL_ARRAY = {
		"/v3/api-docs/**",
		"/swagger-ui/**",
		"/swagger-ui.html",
		"/swagger-resources/**",
		"/webjars/**",
		"/v3/api-docs.yaml",
		"/auth/**",
		"/ws/**",
		"/static/**",
		"/actuator/**",
		"/metrics",
		"/health",
	};

	private final AuthRedisRepository authRedisRepository;
	private final TokenExtractor tokenExtractor;
	private final TokenValidator tokenValidator;

	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource))
			.exceptionHandling(exception -> {
				exception.authenticationEntryPoint(jwtAuthenticationEntryPoint); // 권한 확인
			})
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(PERMIT_URL_ARRAY).permitAll()
				// s3
				.requestMatchers("/api/v1/files/**").permitAll()
				// auth
				.requestMatchers("/api/v1/auth/signup", "/api/v1/auth/reissue",
					"/test/token/**","/api/v1/oauth/callback").permitAll()
				// member
				.requestMatchers("/api/v1/members/verify/**").permitAll()
				// product
				.requestMatchers("/api/v1/products/**", "/api/v1/product/**").permitAll()
				// shipping
				.requestMatchers("/api/v1/shipping/**").permitAll()
				.anyRequest().authenticated())
			.addFilterBefore(new JwtFilter(tokenExtractor, tokenValidator, authRedisRepository),
				UsernamePasswordAuthenticationFilter.class)
			.build();
	}
}
