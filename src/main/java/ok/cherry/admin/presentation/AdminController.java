package ok.cherry.admin.presentation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import ok.cherry.member.application.MemberService;
import ok.cherry.member.domain.Member;
import ok.cherry.product.application.ProductQueryService;
import ok.cherry.product.application.dto.request.ProductCreateRequest;
import ok.cherry.product.domain.Product;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductQueryService productQueryService;
    private final MemberService memberService;

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping("/members")
    public String members(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) String nickname,
        @RequestParam(required = false) String emailAddress,
        Model model
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Member> result = memberService.search(nickname, emailAddress, pageable);

        model.addAttribute("members", result.getContent());
        model.addAttribute("page", result);
        model.addAttribute("nickname", nickname);
        model.addAttribute("emailAddress", emailAddress);

        return "admin/members";
    }

    @GetMapping
    public String products(Model model) {
        model.addAttribute("products", productQueryService.getProducts());
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("productCreateRequest", ProductCreateRequest.empty());
        return "admin/new-product";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productQueryService.getProductById(id);
        model.addAttribute("product", product);
        return "admin/product-detail";
    }
}
