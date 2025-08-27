package ok.cherry.product.domain.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductItemStatus {

	AVAILABLE("대여 가능"),
	RENTED("대여 중");

	private final String description;
}
