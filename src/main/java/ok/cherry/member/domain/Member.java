package ok.cherry.member.domain;

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
import ok.cherry.global.exception.error.DomainException;
import ok.cherry.member.exception.MemberError;

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
		validateNickname(nickname);

		Member member = new Member();
		member.email = new Email(emailAddress);
		member.providerId = requireNonNull(providerId);
		member.provider = requireNonNull(provider);
		member.nickname = requireNonNull(nickname);
		member.status = MemberStatus.ACTIVE;
		member.detail = MemberDetail.create();
		return member;
	}

	public void deactivate() {
		validateIsActive();
		this.status = MemberStatus.DEACTIVATED;
		this.detail.deactivate();
	}

	public void updateEmail(String emailAddress) {
		validateIsActive();
		this.email = new Email(emailAddress);
	}

	public void updateNickname(String nickname) {
		validateIsActive();
		validateNickname(nickname);
		this.nickname = nickname;
	}

	private void validateIsActive() {
		if (status != MemberStatus.ACTIVE) {
			throw new DomainException(MemberError.NOT_ACTIVE);
		}
	}

	private static void validateNickname(String nickname) {
		if (nickname.length() < 2 || nickname.length() > 10) {
			throw new DomainException(MemberError.INVALID_NICKNAME);
		}
	}
}