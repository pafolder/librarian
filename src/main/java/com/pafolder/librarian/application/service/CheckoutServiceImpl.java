package com.pafolder.librarian.application.service;

import static com.pafolder.librarian.infrastructure.controller.admin.AdminCheckoutController.NO_CHECKOUT_FOUND;
import static com.pafolder.librarian.infrastructure.controller.profile.BookController.NO_BOOK_FOUND;

import com.pafolder.librarian.application.command.CheckoutCommand;
import com.pafolder.librarian.domain.model.Book;
import com.pafolder.librarian.domain.model.Checkout;
import com.pafolder.librarian.domain.model.User;
import com.pafolder.librarian.domain.repository.BookRepository;
import com.pafolder.librarian.domain.repository.CheckoutRepository;
import com.pafolder.librarian.domain.service.CheckoutService;
import com.pafolder.librarian.domain.service.UserService;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;

@Named
@Transactional
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

  public static final String REST_URL = "/api/profile/checkout";
  public static final String CHECKOUT_OF_ANOTHER_USER = "Checkout of another user";
  public static final String ALREADY_CHECKIN = "Already checkin";
  public static final int MAX_BORROW_DURATION_IN_DAYS = 14;

  private final BookRepository bookRepository;
  private final CheckoutRepository checkoutRepository;
  private final UserService userService;

  @Override
  public Checkout checkout(int bookId, int userId) {
    var checkout = CheckoutCommand.builder()
        .book(bookRepository.findById(bookId)
            .orElseThrow(() -> new NoSuchElementException(NO_BOOK_FOUND)))
        .user(userService.getById(userId).orElseThrow())
        .build().execute();

    bookRepository.updateAmount(checkout.getBook().getId(), checkout.getBook().getAmount());
    return checkoutRepository.save(checkout);
  }

  @Override
  public void checkin(int checkoutId, int userId) {
    Checkout checkout =
        checkoutRepository
            .findById(checkoutId)
            .orElseThrow(
                () ->
                    new NoSuchElementException(NO_CHECKOUT_FOUND));
    if (checkout.getCheckinDateTime() != null) {
      throw new IllegalStateException(ALREADY_CHECKIN);
    }
    if (!checkout.getUser().getId().equals(userId)) {
      throw new IllegalStateException(CHECKOUT_OF_ANOTHER_USER);
    }
    if (Duration.between(checkout.getCheckoutDateTime(), LocalDateTime.now()).toDays()
        > MAX_BORROW_DURATION_IN_DAYS) {
      User user = userService.getById(userId).orElseThrow();
      userService.updateViolations(user.getId(), user.getViolations() + 1);
    }
    Book book = bookRepository.findById(checkout.getBook().getId()).orElseThrow();
    book.setAmount(book.getAmount() + 1);
    bookRepository.save(book);
    checkout.setCheckinDateTime(LocalDateTime.now());
    checkoutRepository.save(checkout);
  }
}
