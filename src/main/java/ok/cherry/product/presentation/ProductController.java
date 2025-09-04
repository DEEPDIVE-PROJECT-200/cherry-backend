package ok.cherry.product.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ok.cherry.product.application.ProductService;
import ok.cherry.product.application.dto.request.ProductCreateRequest;
import ok.cherry.product.application.dto.response.ProductCreateResponse;
import ok.cherry.global.swagger.product.ProductControllerDoc;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController implements ProductControllerDoc {

	private final ProductService productService;

	@PostMapping("/product")
	public ResponseEntity<ProductCreateResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
		ProductCreateResponse response = productService.createProduct(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
