package com.pafolder.librarian.application.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ViolationLimitValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ViolationLimit {
  String message() default "Borrowing is prohibited because the violation limit exceeded";
  int maxViolation() default 1;
  int borrowDurationDays() default 14;
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
