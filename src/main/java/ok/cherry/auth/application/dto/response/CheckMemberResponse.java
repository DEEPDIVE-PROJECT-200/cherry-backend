package ok.cherry.auth.application.dto.response;

public record CheckMemberResponse(

	String providerId,
	boolean isMember
) {
}
