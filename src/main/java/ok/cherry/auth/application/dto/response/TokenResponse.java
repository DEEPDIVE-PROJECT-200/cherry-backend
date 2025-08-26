package ok.cherry.auth.application.dto.response;

public record TokenResponse(

	String tokenType,
	String accessToken,
	Long accessTokenExpiresIn,
	String refreshToken
) {
}
