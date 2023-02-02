package com.pafolder.cbr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.pafolder.cbr.model.Book;
import com.pafolder.cbr.model.Checkout;
import com.pafolder.cbr.model.User;
import com.pafolder.cbr.repository.BookRepository;
import com.pafolder.cbr.repository.CheckoutRepository;
import com.pafolder.cbr.security.UserDetailsImpl;
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

import static com.pafolder.cbr.controller.BookController.NO_BOOK_FOUND;

@RestController
@AllArgsConstructor
@Tag(name = "profile-checkout-controller")
@RequestMapping(value = CheckoutController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class CheckoutController {
    public static final String REST_URL = "/api/profile/checkout";
    public static final String NO_BOOKS_BORROWED = "No books borrowed";
    public static final String BOOK_IS_TEMPORARY_UNAVAILABLE = "Book is temporary unavailable";
    public static final String BORROWING_PROHIBITED = "Borrowing is prohibited because the violation limit exceeded";
    private static final int MAX_BORROW_DURATION_IN_DAYS = 14;
    public static final int MAX_VIOLATIONS = 2;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private BookRepository bookRepository;
    private CheckoutRepository checkoutRepository;

    @GetMapping
    @Operation(summary = "Get authenticated user's borrowed books", security = {@SecurityRequirement(name = "basicScheme")})
    public MappingJacksonValue get(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("get()");
        List<Checkout> checkouts = checkoutRepository.findAllByUser(userDetails.getUser());
        if (checkouts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, NO_BOOKS_BORROWED);
        }
        return getFilteredCheckoutsJson(checkouts);
    }

    private MappingJacksonValue getFilteredCheckoutsJson(List<Checkout> checkouts) {
        SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("checkoutJsonFilter",
                        SimpleBeanPropertyFilter.filterOutAllExcept("checkout", "book"))
                .addFilter("bookJsonFilter",
                        SimpleBeanPropertyFilter.filterOutAllExcept("id", "author", "title"));
        new ObjectMapper().setFilterProvider(filterProvider);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(checkouts);
        mappingJacksonValue.setFilters(filterProvider);
        return mappingJacksonValue;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(summary = "Borrow a book by authenticated user",
            security = {@SecurityRequirement(name = "basicScheme")})
    @Parameter(name = "id", description = "If User has more than " + MAX_VIOLATIONS +
            " no more borrowing possible")
    @Transactional
    public ResponseEntity<MappingJacksonValue> create(@RequestParam int id,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("create()");
        User user = userDetails.getUser();
        if (user.getViolations() + checkViolationsInPendingCheckouts(user) > MAX_VIOLATIONS) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, BORROWING_PROHIBITED);
        }
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, NO_BOOK_FOUND));
        if (book.getAmount() == 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, BOOK_IS_TEMPORARY_UNAVAILABLE);
        }
        Checkout created = checkoutRepository.save(
                new Checkout(null, user, book, LocalDateTime.now(), null));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(getFilteredCheckoutsJson(List.of(created)));
    }

    public int checkViolationsInPendingCheckouts(User user) {
        int count = 0;
        List<Checkout> currentCheckouts = checkoutRepository.findAllByUser(user);
        if (!currentCheckouts.isEmpty()) {
            for (Checkout ch : currentCheckouts) {
                count += Duration.between(ch.getCheckoutDateTime(), LocalDateTime.now()).toDays() >
                        MAX_BORROW_DURATION_IN_DAYS ? 1 : 0;
            }
        }
        return count;
    }
/*
    @PutMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Operation(summary = "Return (checkin) the Book", security = {@SecurityRequirement(name = "basicScheme")})
    @Parameter(name = "bookId", description = "Book Id")
    @Transactional
    public void checkin(@RequestParam int bookId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("checkin()");
        Checkout checkout = checkoutRepository.findByBookId(bookId);
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, NO_VOTE_FOUND));
//        Book menu = menuRepository.findByDateAndRestaurantId(LocalDate.now(), restaurantId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
//                        NO_MENU_RESTAURANT_FOUND));
//        vote.setMenu(menu);
        checkoutRepository.save(checkout);
    }

    @DeleteMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete authenticated user's vote", security = {@SecurityRequirement(name = "basicScheme")})
    @Transactional
    public void delete(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("delete");
        throwExceptionIfLateToVote();
        Checkout vote = voteRepository.findByDateAndUser(LocalDate.now(), userDetails.getUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, NO_VOTE_FOUND));
        voteRepository.delete(vote);
    } */
}
