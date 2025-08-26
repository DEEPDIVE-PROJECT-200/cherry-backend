package ok.cherry.auth.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ok.cherry.auth.exception.TokenError;
import ok.cherry.global.exception.error.BusinessException;
import ok.cherry.global.redis.AuthRedisRepository;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final TokenExtractor tokenExtractor;
	private final TokenValidator tokenValidator;
	private final AuthRedisRepository authRedisRepository;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String jwt = tokenExtractor.resolveToken(request);

		if (StringUtils.hasText(jwt) && tokenValidator.validateToken(jwt)) {
			// 블랙리스트 형식으로 redis 에 해당 accessToken logout 여부 확인
			Object isLogout = authRedisRepository.getLogoutToken(jwt);

			// 로그아웃이 되어 있지 않은 경우 토큰 정상 작동
			if (ObjectUtils.isEmpty(isLogout)) {
				Authentication authentication = tokenExtractor.getAuthentication(jwt);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				throw new BusinessException(TokenError.ALREADY_LOGOUT);
			}
		}

		filterChain.doFilter(request, response);
	}
}
