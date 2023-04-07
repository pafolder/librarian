package com.pafolder.librarian.domain.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = BookingLimitValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BookingLimit {

  int maxBooksAllowedAtOnce() default 3;

  String message() default "Borrowing is prohibited because the limit of {maxBooksAllowedAtOnce} books reached";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
