package ok.cherry.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieManager {

	private static final String COOKIE_PATH = "/";

	@Value("${cookie.domain}")
	private String cookieDomain;

	@Value("${jwt.refresh-token-expiration-seconds}")
	private int refreshTokenExpirationSeconds;


	public void setCookie(HttpServletResponse response, String name, String value) {
		String cookieHeader = String.format(
			"%s=%s; Max-Age=%d; Path=%s; Domain=%s; Secure; HttpOnly; SameSite=None",
			name, value, refreshTokenExpirationSeconds, COOKIE_PATH, cookieDomain
		);
		response.setHeader("Set-Cookie", cookieHeader);
	}

	public void removeCookie(HttpServletResponse response, String name) {
		String cookieHeader = String.format(
			"%s=; Max-Age=0; Path=%s; Domain=%s; Secure; HttpOnly; SameSite=None",
			name, COOKIE_PATH, cookieDomain
		);
		response.setHeader("Set-Cookie", cookieHeader);
	}
}