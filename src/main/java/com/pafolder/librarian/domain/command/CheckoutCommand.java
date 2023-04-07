package com.pafolder.librarian.domain.command;

import com.pafolder.librarian.domain.validator.BookBorrowedChecking;
import com.pafolder.librarian.domain.validator.BookingLimit;
import com.pafolder.librarian.domain.validator.ViolationLimit;
import com.pafolder.librarian.domain.model.Book;
import com.pafolder.librarian.domain.model.Checkout;
import com.pafolder.librarian.domain.model.User;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class CheckoutCommand implements Command<Checkout> {

  private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

  @BookingLimit(maxBooksAllowedAtOnce = 3)
  @ViolationLimit(maxViolation = 1, borrowDurationDays = 14)
  private final User user;

  @BookBorrowedChecking
  private final Book book;

  @Override
  public Checkout execute() {
    validate();
    book.setAmount(book.getAmount() - 1);
    return new Checkout(null, user, book, LocalDateTime.now(), null);
  }

  private void validate() {
    Validator validator = factory.getValidator();

    var errors = validator.validate(this);

    if (errors.size() > 0) {
      throw new ConstraintViolationException(errors);
    }
  }

}
