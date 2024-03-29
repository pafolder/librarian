package com.pafolder.librarian.infrastructure.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private final MessageSource messageSource;

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    ProblemDetail body = ex.updateAndGetBody(this.messageSource, LocaleContextHolder.getLocale());
    Map<String, String> invalidParams = new LinkedHashMap<>();
    for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
      invalidParams.put(
          error.getObjectName(),
          messageSource.getMessage(
              error.getCode(),
              error.getArguments(),
              error.getDefaultMessage(),
              LocaleContextHolder.getLocale()));
    }
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      invalidParams.put(
          error.getField(),
          messageSource.getMessage(
              error.getCode(),
              error.getArguments(),
              error.getDefaultMessage(),
              LocaleContextHolder.getLocale()));
    }
    body.setProperty("invalid_params", invalidParams);
    body.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
    return handleExceptionInternal(ex, body, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<?> handleIllegalStateException(
      IllegalStateException ex, WebRequest request) {
    return handleExceptionInternal(
        ex,
        createProblemDetail(ex, HttpStatus.CONFLICT, ex.getMessage(), null, null, request),
        new HttpHeaders(),
        HttpStatus.CONFLICT,
        request);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<?> handleNoSuchElementException(
      NoSuchElementException ex, WebRequest request) {
    return handleExceptionInternal(
        ex,
        createProblemDetail(ex, HttpStatus.NO_CONTENT, ex.getMessage(), null, null, request),
        new HttpHeaders(),
        HttpStatus.NO_CONTENT,
        request);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<?> handleResponseStatusException(
      ResponseStatusException ex, WebRequest request) {
    return handleExceptionInternal(
        ex,
        createProblemDetail(ex, ex.getStatusCode(), ex.getReason(), null, null, request),
        new HttpHeaders(),
        ex.getStatusCode(),
        request);
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<?> handleEmptyResultDataAccessException(
      DataAccessException ex, WebRequest request) {
    return handleExceptionInternal(
        ex,
        createProblemDetail(
            ex, HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), null, null, request),
        new HttpHeaders(),
        HttpStatus.UNPROCESSABLE_ENTITY,
        request);
  }

  @ExceptionHandler({DataIntegrityViolationException.class})
  public ResponseEntity<Object> handleDataIntegrityViolation(
      DataIntegrityViolationException ex, WebRequest request) {
    List<String> errors = new ArrayList<>();
    errors.add(/*ex.getCause() + " : " + */ ex.getMessage());
    ApiError apiError =
        new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, ex.getLocalizedMessage(), errors);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolation(
      ConstraintViolationException ex, WebRequest request) {
    List<String> errors = new ArrayList<>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      //errors.add(violation.getMessage());
      errors.add(
          violation.getRootBeanClass().getName()
              + " "
              + violation.getPropertyPath()
              + ": "
              + violation.getMessage());
    }
    ApiError apiError =
        new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, ex.getLocalizedMessage(), errors);
    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
  }

  @Getter
  @AllArgsConstructor
  public static class ApiError {
    private HttpStatus status;
    private String message;
    private List<String> errors;
  }
}
