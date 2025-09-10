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
import ok.cherry.rental.application.RentalService;
import ok.cherry.rental.domain.Rental;
import ok.cherry.payment.application.PaymentService;
import ok.cherry.payment.domain.Payment;
import ok.cherry.shipping.application.ShippingService;
import ok.cherry.shipping.domain.Shipping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductQueryService productQueryService;
    private final MemberService memberService;
    private final RentalService rentalService;
    private final PaymentService paymentService;
    private final ShippingService shippingService;

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

    @GetMapping("/rentals")
    public String rentals(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) String orderNumber,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        Model model
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Rental> result = rentalService.search(orderNumber, startDate, endDate, pageable);

        model.addAttribute("rentals", result.getContent());
        model.addAttribute("page", result);
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/rentals";
    }

    @GetMapping("/rentals/{id}")
    public String rentalDetail(@PathVariable Long id, Model model) {
        Rental rental = rentalService.findRentalById(id);
        Payment payment = paymentService.getPaymentByRentalId(id);
        Shipping outboundShipping = shippingService.getOutboundShippingByRental(rental.getId());

        model.addAttribute("rental", rental);
        model.addAttribute("payment", payment);
        model.addAttribute("outboundShipping", outboundShipping);

        return "admin/rental-detail";
    }
}
