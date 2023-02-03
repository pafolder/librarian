package com.pafolder.librarian.controller.profile;

import com.pafolder.librarian.model.Book;
import com.pafolder.librarian.model.Checkout;
import com.pafolder.librarian.model.User;
import com.pafolder.librarian.repository.BookRepository;
import com.pafolder.librarian.repository.CheckoutRepository;
import com.pafolder.librarian.security.UserDetailsImpl;
import com.pafolder.librarian.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.pafolder.librarian.controller.admin.AdminCheckoutController.NO_CHECKOUT_FOUND;
import static com.pafolder.librarian.controller.profile.BookController.NO_BOOK_FOUND;
import static com.pafolder.librarian.util.JsonFilter.getFilteredCheckoutsJson;

@RestController
@AllArgsConstructor
@Tag(name = "2 profile-checkout-controller")
@RequestMapping(value = CheckoutController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class CheckoutController {
    public static final String REST_URL = "/api/profile/checkout";
    public static final String BOOK_IS_ALREADY_BORROWED = "Book is already borrowed by another user";
    public static final String BORROWING_PROHIBITED = "Borrowing is prohibited because the violation limit exceeded";
    public static final String CHECKOUT_OF_ANOTHER_USER = "Checkout of another user";
    public static final int MAX_BOOKS_ALLOWED_AT_ONCE = 3;
    public static final String BORROWING_PROHIBITED_LIMIT_REACHED =
            "Borrowing is prohibited because the limit of " + MAX_BOOKS_ALLOWED_AT_ONCE + " books reached";
    private static final int MAX_BORROW_DURATION_IN_DAYS = 14;
    public static final int MAX_VIOLATIONS = 1;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private BookRepository bookRepository;
    private CheckoutRepository checkoutRepository;
    private UserServiceImpl userService;

    @GetMapping
    @Operation(summary = "Get authenticated User's borrowed books",
            security = {@SecurityRequirement(name = "basicScheme")})
    public MappingJacksonValue get(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("get()");
        List<Checkout> checkouts = checkoutRepository.findAllActiveByUserId(userDetails.getUser().getId());
        return getFilteredCheckoutsJson(false, checkouts);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(summary = "Borrow a Book by authenticated User",
            security = {@SecurityRequirement(name = "basicScheme")})
    @Parameter(name = "id", description = "Id of the Book to checkout. If User has more than " + MAX_VIOLATIONS +
            " borrowed books, no more checkouts possible")
    @Transactional
    public ResponseEntity<MappingJacksonValue> create(@RequestParam int id,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("create()");
        User user = userService.getById(userDetails.getUser().getId()).orElseThrow();
        if (getFutureViolations(user, checkoutRepository) > MAX_VIOLATIONS) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, BORROWING_PROHIBITED);
        }
        if (checkoutRepository.findAllActiveByUserId(user.getId()).size() >= MAX_BOOKS_ALLOWED_AT_ONCE) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, BORROWING_PROHIBITED_LIMIT_REACHED);
        }
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, NO_BOOK_FOUND));
        if (book.getAmount() == 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, BOOK_IS_ALREADY_BORROWED);
        }
        book.setAmount(book.getAmount() - 1);
        bookRepository.updateAmount(book.getId(), book.getAmount());
        Checkout created = checkoutRepository.save(
                new Checkout(null, user, book, LocalDateTime.now(), null));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(getFilteredCheckoutsJson(false, created));
    }

    public static int getFutureViolations(User user, CheckoutRepository checkoutRepository) {
        int[] count = new int[]{user.getViolations()};
        List<Checkout> currentCheckouts = checkoutRepository.findAllActiveByUserId(user.getId());
        if (!currentCheckouts.isEmpty()) {
            currentCheckouts.forEach(checkout ->
                    count[0] += Duration.between(checkout.getCheckoutDateTime(), LocalDateTime.now()).toDays() >
                            MAX_BORROW_DURATION_IN_DAYS ? 1 : 0);
        }
        return count[0];
    }

    @PutMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Operation(summary = "Return (check in) the Book by authenticated User", security = {@SecurityRequirement(name = "basicScheme")})
    @Parameter(name = "id", description = "Checkout Id")
    @Transactional
    public void checkin(@RequestParam int id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("checkin()");
        Checkout checkout = checkoutRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, NO_CHECKOUT_FOUND));
        if (!checkout.getUser().equals(userDetails.getUser())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, CHECKOUT_OF_ANOTHER_USER);
        }
        if (Duration.between(checkout.getCheckoutDateTime(), LocalDateTime.now()).toDays() >
                MAX_BORROW_DURATION_IN_DAYS) {
            User user = userService.getById(userDetails.getUser().getId()).orElseThrow();
            userService.updateViolations(user.getId(), user.getViolations() + 1);
        }
        Book book = bookRepository.findById(checkout.getBook().getId()).orElseThrow();
        book.setAmount(book.getAmount() + 1);
        bookRepository.save(book);
        checkout.setCheckinDateTime(LocalDateTime.now());
        checkoutRepository.save(checkout);
    }
}
