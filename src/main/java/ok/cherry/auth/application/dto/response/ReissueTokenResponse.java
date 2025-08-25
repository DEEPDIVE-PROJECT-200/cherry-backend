package ok.cherry.auth.application.dto.response;

public record ReissueTokenResponse(

	String grantType,
	String accessToken,
	Long accessTokenExpiresIn
) {
}
