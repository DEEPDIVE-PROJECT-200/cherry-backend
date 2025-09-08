package ok.cherry.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AccessToken 응답 DTO")
public record AccessTokenResponse(

	@Schema(description = "토큰 타입", example = "Bearer")
	String tokenType,

	@Schema(description = "발급된 Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	String accessToken,

	@Schema(description = "Access Token 만료 시간 (초 단위)", example = "3600")
	Long accessTokenExpiresInSeconds
) {
	public static AccessTokenResponse of(TokenResponse tokenResponse) {
		return new AccessTokenResponse(
			tokenResponse.tokenType(),
			tokenResponse.accessToken(),
			tokenResponse.accessTokenExpiresInSeconds()
		);
	}
}
