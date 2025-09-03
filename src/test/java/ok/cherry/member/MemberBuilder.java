package ok.cherry.member;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ok.cherry.member.domain.Member;
import ok.cherry.member.domain.Provider;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberBuilder {

	public String providerId = "12345";
	public Provider provider = Provider.KAKAO;
	public String email = "test@test.com";
	public String nickname = "tester";

	public static Member create() {
		return builder().build();
	}

	public static MemberBuilder builder() {
		return new MemberBuilder();
	}

	public Member build() {
		return Member.register(providerId, provider, email, nickname);
	}

	public MemberBuilder withProviderId(String providerId) {
		this.providerId = providerId;
		return this;
	}

	public MemberBuilder withProvider(Provider provider) {
		this.provider = provider;
		return this;
	}

	public MemberBuilder withEmail(String email) {
		this.email = email;
		return this;
	}

	public MemberBuilder withNickname(String nickname) {
		this.nickname = nickname;
		return this;
	}
}