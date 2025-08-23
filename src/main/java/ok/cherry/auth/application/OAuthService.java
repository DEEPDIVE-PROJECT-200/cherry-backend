package ok.cherry.auth.application;

import ok.cherry.auth.application.dto.response.CheckMemberResponse;

public interface OAuthService {

	String getAccessToken(String code);

	String getProviderId(String accessToken);

	CheckMemberResponse checkMember(String providerId);
}
