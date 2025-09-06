package ok.cherry.product.presentation;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.global.swagger.product.ProductControllerDoc;
import ok.cherry.product.application.ProductCreateService;
import ok.cherry.product.application.ProductQueryService;
import ok.cherry.product.application.dto.request.ProductCreateRequest;
import ok.cherry.product.application.dto.request.ProductSortType;
import ok.cherry.product.application.dto.response.ProductCreateResponse;
import ok.cherry.product.application.dto.response.ProductSearchResponse;
import ok.cherry.product.domain.type.Brand;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController implements ProductControllerDoc {

	private final ProductCreateService productCreateService;
	private final ProductQueryService productQueryService;

	@PostMapping("/product")
	public ResponseEntity<ProductCreateResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
		ProductCreateResponse response = productCreateService.createProduct(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/products")
	public ResponseEntity<ProductSearchResponse> searchProducts(
		@RequestParam(required = false) List<Brand> brands,
		@RequestParam ProductSortType sortType,
		@RequestParam(required = false) Long lastProductId,
		@RequestParam(defaultValue = "6") int limit
	) {
		ProductSearchResponse response =
			productQueryService.searchProductsWithConditions(brands, sortType, lastProductId, limit);
		return ResponseEntity.ok(response);
	}
}