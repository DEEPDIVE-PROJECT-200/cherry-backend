package ok.cherry.member.domain;

import static java.util.Objects.*;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	@Column(nullable = false)
	private Provider provider;

	@Column(nullable = false, unique = true, updatable = false)
	private String providerId;

	@Embedded
	private Email email;

	@Column(nullable = false, unique = true)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MemberStatus memberStatus;

	@Embedded
	private MemberDetail detail;

	public static Member register(String providerId, Provider provider, String emailAddress, String nickname) {
		validateNickname(nickname);

		Member member = new Member();
		member.email = new Email(emailAddress);
		member.providerId = requireNonNull(providerId);
		member.provider = requireNonNull(provider);
		member.nickname = requireNonNull(nickname);
		member.memberStatus = MemberStatus.ACTIVE;
		member.detail = MemberDetail.create();
		return member;
	}

	private static void validateNickname(String nickname) {
		if (nickname == null || nickname.length() < 2 || nickname.length() > 10) {
			throw new DomainException(MemberError.INVALID_NICKNAME);
		}
	}

	public void deactivate() {
		validateIsActive();
		this.memberStatus = MemberStatus.DEACTIVATED;
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
		if (memberStatus != MemberStatus.ACTIVE) {
			throw new DomainException(MemberError.NOT_ACTIVE);
		}
	}
}