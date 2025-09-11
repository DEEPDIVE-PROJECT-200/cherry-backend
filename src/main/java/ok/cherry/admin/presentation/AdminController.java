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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import ok.cherry.member.application.MemberService;
import ok.cherry.member.domain.Member;
import ok.cherry.product.application.ProductQueryService;
import ok.cherry.product.application.dto.request.ProductCreateRequest;
import ok.cherry.product.domain.Product;
import ok.cherry.rental.application.RentalApplicationService;
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
    private final RentalApplicationService rentalApplicationService;

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
        @RequestParam(required = false) String providerId,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        Model model
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Rental> result = rentalService.search(orderNumber, providerId, startDate, endDate, pageable);

        model.addAttribute("rentals", result.getContent());
        model.addAttribute("page", result);
        model.addAttribute("orderNumber", orderNumber);
        model.addAttribute("providerId", providerId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/rentals";
    }

    @GetMapping("/rentals/{id}")
    public String rentalDetail(@PathVariable Long id, Model model) {
        Rental rental = rentalService.findRentalById(id);
        Payment payment = paymentService.getPaymentByRentalId(id);
        Shipping outboundShipping = shippingService.getOutboundShippingByRental(rental.getId());
        Shipping inboundShipping = shippingService.getInboundShippingByRentalOrNull(rental.getId());

        model.addAttribute("rental", rental);
        model.addAttribute("payment", payment);
        model.addAttribute("outboundShipping", outboundShipping);
        model.addAttribute("inboundShipping", inboundShipping);

        return "admin/rental-detail";
    }

    @PostMapping("/rentals/{id}/shipping/start")
    public String startOutboundShipping(@PathVariable Long id) {
        Shipping shipping = shippingService.getOutboundShippingByRental(id);
        shippingService.startShipping(shipping.getId());
        return "redirect:/admin/rentals/" + id;
    }

    @PostMapping("/rentals/{id}/shipping/complete")
    public String completeOutboundShipping(@PathVariable Long id) {
        Shipping shipping = shippingService.getOutboundShippingByRental(id);
        shippingService.completeShipping(shipping.getId());
        return "redirect:/admin/rentals/" + id;
    }

    @PostMapping("/rentals/{id}/return/shipping/start")
    public String startInboundShipping(@PathVariable Long id) {
        Shipping shipping = shippingService.getInboundShippingByRentalOrNull(id);
        shippingService.startShipping(shipping.getId());
        return "redirect:/admin/rentals/" + id;
    }

    @PostMapping("/rentals/{id}/return/shipping/complete")
    public String completeInboundShipping(@PathVariable Long id) {
        Shipping shipping = shippingService.getInboundShippingByRentalOrNull(id);
        shippingService.completeShipping(shipping.getId());
        return "redirect:/admin/rentals/" + id;
    }

    @PostMapping("/rentals/{id}/inspect/complete")
    public String completeInspection(@PathVariable Long id) {
        rentalApplicationService.completeRental(id);
        return "redirect:/admin/rentals/" + id;
    }

    @PostMapping("/rentals/{id}/review/complete")
    public String completeReview(@PathVariable Long id) {
        rentalService.completeReview(id);
        return "redirect:/admin/rentals/" + id;
    }

    @PostMapping("/rentals/{id}/return/initiate")
    public String initiateEarlyReturn(@PathVariable Long id) {
        rentalApplicationService.initiateEarlyReturn(id);
        return "redirect:/admin/rentals/" + id;
    }
}
