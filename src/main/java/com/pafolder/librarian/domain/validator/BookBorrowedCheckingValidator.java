package com.pafolder.librarian.domain.validator;

import com.pafolder.librarian.domain.model.Book;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BookBorrowedCheckingValidator  implements
    ConstraintValidator<BookBorrowedChecking, Book> {

  @Override
  public void initialize(BookBorrowedChecking constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(Book book, ConstraintValidatorContext constraintValidatorContext) {
    return book.getAmount() > 0;
  }
}
