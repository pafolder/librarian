package com.pafolder.librarian.application.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = BookBorrowedCheckingValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BookBorrowedChecking {

  String message() default "Book is already borrowed by another user";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
