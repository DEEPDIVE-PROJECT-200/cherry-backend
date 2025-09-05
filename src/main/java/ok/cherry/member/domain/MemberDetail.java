package ok.cherry.member.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetail {

	@Column(nullable = false)
	private LocalDateTime registeredAt;

	private LocalDateTime deactivatedAt;

	static MemberDetail create() {
		MemberDetail memberDetail = new MemberDetail();
		memberDetail.registeredAt = LocalDateTime.now();
		return memberDetail;
	}

	void deactivate() {
		this.deactivatedAt = LocalDateTime.now();
	}
}