package com.pafolder.librarian.application.validator;

import com.pafolder.librarian.domain.model.Checkout;
import com.pafolder.librarian.domain.model.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ViolationLimitValidator implements
    ConstraintValidator<ViolationLimit, User> {

  private int maxViolations;
  private int maxBorrowDurationInDays;

  @Override
  public void initialize(ViolationLimit constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);

    maxViolations = constraintAnnotation.maxViolation();
    maxBorrowDurationInDays = constraintAnnotation.borrowDurationDays();
  }

  @Override
  public boolean isValid(User user, ConstraintValidatorContext constraintValidatorContext) {
    int[] count = new int[] {user.getViolations()};
    List<Checkout> currentCheckouts = user.activeCheckouts();
    if (!currentCheckouts.isEmpty()) {
      currentCheckouts.forEach(
          checkout ->
              count[0] +=
                  Duration.between(checkout.getCheckoutDateTime(), LocalDateTime.now()).toDays()
                      > maxBorrowDurationInDays
                      ? 1
                      : 0);
    }

    return !(count[0] > maxViolations);
  }
}
