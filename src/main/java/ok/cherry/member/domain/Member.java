package ok.cherry.member.domain;

import static io.jsonwebtoken.lang.Assert.*;
import static java.util.Objects.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private Provider provider;

	private String providerId;

	@Embedded
	private Email email;

	private String nickname;

	@Enumerated(EnumType.STRING)
	private MemberStatus status;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private MemberDetail detail;

	public static Member register(String providerId, Provider provider, String emailAddress, String nickname) {
		Member member = new Member();

		member.email = new Email(emailAddress);
		member.providerId = requireNonNull(providerId);
		member.provider = requireNonNull(provider);
		member.nickname = requireNonNull(nickname);
		state(nickname.length() >= 2 && nickname.length() <= 10, "닉네임은 2 ~ 10글자만 가능합니다");

		member.status = MemberStatus.ACTIVE;
		member.detail = MemberDetail.create();
		return member;
	}

	public void deactivate() {
		state(status == MemberStatus.ACTIVE, "ACTIVE 상태가 아닙니다");

		this.status = MemberStatus.DEACTIVATED;
		this.detail.deactivate();
	}

	public void updateEmail(String emailAddress) {
		state(status == MemberStatus.ACTIVE, "ACTIVE 상태가 아닙니다");

		this.email = new Email(emailAddress);
	}

	public void updateNickname(String nickname) {
		state(status == MemberStatus.ACTIVE, "ACTIVE 상태가 아닙니다");
		state(nickname.length() >= 2 && nickname.length() <= 10, "닉네임은 2 ~ 10글자만 가능합니다");

		this.nickname = requireNonNull(nickname);
	}
}
