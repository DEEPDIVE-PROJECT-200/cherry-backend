package ok.cherry.product.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {

	HEADPHONE("헤드폰");

	private final String description;
}
