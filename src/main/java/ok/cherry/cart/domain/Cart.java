package ok.cherry.cart.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ok.cherry.member.domain.Member;
import ok.cherry.product.domain.Product;
import ok.cherry.product.domain.type.Color;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false)
	private BigDecimal price;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Color color;

	public static Cart create(Member member, Product product, BigDecimal price, Color color) {
		Cart cart = new Cart();
		cart.member = member;
		cart.product = product;
		cart.price = price;
		cart.color = color;
		return cart;
	}
}
