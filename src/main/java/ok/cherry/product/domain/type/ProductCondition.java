package ok.cherry.product.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCondition {

	S_GRADE("S급 (최상급 중고)"),
	A_GRADE("A급 (양호한 상태)"),
	B_GRADE("B급 (사용감 있음)");

	private final String description;
}
