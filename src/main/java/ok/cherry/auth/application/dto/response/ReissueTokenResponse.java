package ok.cherry.auth.application.dto.response;

public record ReissueTokenResponse(

	String tokenType,
	String accessToken,
	Long accessTokenExpiresIn
) {
}
