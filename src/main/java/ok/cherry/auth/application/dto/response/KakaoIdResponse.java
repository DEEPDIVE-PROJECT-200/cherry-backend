package ok.cherry.auth.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoIdResponse(

	@JsonProperty("id")
	String providerId

) {
}
