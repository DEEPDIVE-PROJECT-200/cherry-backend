package ok.cherry.cart;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ok.cherry.cart.domain.Cart;
import ok.cherry.member.MemberBuilder;
import ok.cherry.member.domain.Member;
import ok.cherry.product.ProductBuilder;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.type.Color;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartBuilder {

	public Member member = MemberBuilder.create();
	public Product product = ProductBuilder.create();
	public BigDecimal price = BigDecimal.valueOf(10000);
	public Color color = Color.BLACK;

	public static Cart create() {
		return builder().build();
	}
	
	public static CartBuilder builder() {
		return new CartBuilder();
	}

	public Cart build() {
		return Cart.create(member, product, price, color);
	}

	public CartBuilder withMember(Member member) {
		this.member = member;
		return this;
	}

	public CartBuilder withProduct(Product product) {
		this.product = product;
		return this;
	}

	public CartBuilder withPrice(BigDecimal price) {
		this.price = price;
		return this;
	}

	public CartBuilder withColor(Color color) {
		this.color = color;
		return this;
	}
}