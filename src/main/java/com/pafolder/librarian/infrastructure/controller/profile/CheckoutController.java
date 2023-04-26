package com.pafolder.librarian.infrastructure.controller.profile;

import static com.pafolder.librarian.infrastructure.controller.ControllerUtil.getFilteredCheckoutsJson;

import com.pafolder.librarian.domain.model.Checkout;
import com.pafolder.librarian.infrastructure.repository.CheckoutRepository;
import com.pafolder.librarian.domain.service.CheckoutService;
import com.pafolder.librarian.infrastructure.controller.view.CheckoutView;
import com.pafolder.librarian.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@AllArgsConstructor
@Tag(name = "2 profile-checkout-controller")
@RequestMapping(value = CheckoutController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class CheckoutController {

  public static final String REST_URL = "/api/profile/checkout";
  public static final String BOOK_IS_ALREADY_BORROWED = "Book is already borrowed by another user";
  public static final String BORROWING_PROHIBITED =
      "Borrowing is prohibited because the violation limit exceeded";
  public static final String CHECKOUT_OF_ANOTHER_USER = "Checkout of another user";
  public static final int MAX_VIOLATIONS = 1;
  public static final int MAX_BOOKS_ALLOWED_AT_ONCE = 3;
  public static final int MAX_BORROW_DURATION_IN_DAYS = 14;
  public static final String BORROWING_PROHIBITED_LIMIT_REACHED =
      "Borrowing is prohibited because the limit of "
          + MAX_BOOKS_ALLOWED_AT_ONCE
          + " books reached";
  private final Logger log = LoggerFactory.getLogger(getClass());
  private CheckoutRepository checkoutRepository;
  private CheckoutService checkoutService;

  @GetMapping
  @Operation(
      summary = "Get authenticated User's borrowed books",
      security = {@SecurityRequirement(name = "basicScheme")})
  public MappingJacksonValue get(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    log.info("get()");
    List<Checkout> checkouts =
        checkoutRepository.findAllActiveByUserId(userDetails.getUser().getId());
    return getFilteredCheckoutsJson(false, checkouts.stream()
        .map(CheckoutView::new).collect(Collectors.toList()));
  }

  @PostMapping
  @ResponseStatus(value = HttpStatus.CREATED)
  @Operation(
      summary = "Borrow a Book by authenticated User",
      security = {@SecurityRequirement(name = "basicScheme")})
  @Parameter(
      name = "id",
      description =
          "Id of the Book to checkout. If User has more than "
              + MAX_VIOLATIONS
              + " borrowed books, no more checkouts possible")
  public ResponseEntity<MappingJacksonValue> create(
      @RequestParam int id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    log.info("create()");
    var checkout = checkoutService.checkout(id, userDetails.getUser().getId());

    URI uriOfNewResource =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path(REST_URL + "/{id}")
            .buildAndExpand(checkout.getId())
            .toUri();
    return ResponseEntity.created(uriOfNewResource)
        .body(getFilteredCheckoutsJson(false, new CheckoutView(checkout)));
  }

  @PutMapping
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Return (check in) the Book by authenticated User",
      security = {@SecurityRequirement(name = "basicScheme")})
  @Parameter(name = "id", description = "Checkout Id")
  @Transactional
  public void checkin(@RequestParam int id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    log.info("checkin()");

    checkoutService.checkin(id, userDetails.getUser().getId());
  }
}
