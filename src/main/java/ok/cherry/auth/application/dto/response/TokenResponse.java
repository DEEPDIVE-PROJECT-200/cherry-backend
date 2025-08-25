package ok.cherry.auth.application.dto.response;

public record TokenResponse(

	String grantType,
	String accessToken,
	Long accessTokenExpiresIn,
	String refreshToken
) {
}
