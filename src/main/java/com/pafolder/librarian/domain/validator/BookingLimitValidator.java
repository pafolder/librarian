package com.pafolder.librarian.domain.validator;

import com.pafolder.librarian.domain.model.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BookingLimitValidator implements
    ConstraintValidator<BookingLimit, User> {

  private int maxBooksAllowedAtOnce;

  @Override
  public void initialize(BookingLimit constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);

    maxBooksAllowedAtOnce = constraintAnnotation.maxBooksAllowedAtOnce();
  }

  @Override
  public boolean isValid(User user, ConstraintValidatorContext constraintValidatorContext) {
    return user.activeCheckouts().size() < maxBooksAllowedAtOnce;
  }
}
