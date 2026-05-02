package com.credit.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    List<ValidationErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
        .map(this::toValidationDetail)
        .toList();

    log.warn("Validation failed for {}: {}", request.getRequestURI(), details);

    ErrorResponse body = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Validation Failed")
        .message("Запрос содержит некорректные данные")
        .path(request.getRequestURI())
        .details(details)
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {

    List<ValidationErrorDetail> details = ex.getConstraintViolations().stream()
        .map(this::toValidationDetail)
        .toList();

    log.warn("Constraint violation for {}: {}", request.getRequestURI(), details);

    ErrorResponse body = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Constraint Violation")
        .message("Параметры запроса не прошли валидацию")
        .path(request.getRequestURI())
        .details(details)
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
      NotFoundException ex, HttpServletRequest request) {

    log.warn("Not found: {} (path={})", ex.getMessage(), request.getRequestURI());

    ErrorResponse body = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error("Not Found")
        .message(ex.getMessage())
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusiness(
      BusinessException ex, HttpServletRequest request) {

    log.warn("Business error: {} (path={})", ex.getMessage(), request.getRequestURI());

    ErrorResponse body = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Business Rule Violation")
        .message(ex.getMessage())
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParam(
      MissingServletRequestParameterException ex, HttpServletRequest request) {

    ErrorResponse body = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Missing Request Parameter")
        .message("Отсутствует обязательный параметр: " + ex.getParameterName())
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

    String requiredType = ex.getRequiredType() != null
        ? ex.getRequiredType().getSimpleName()
        : "unknown";

    ErrorResponse body = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Type Mismatch")
        .message("Параметр '" + ex.getName() + "' должен быть типа " + requiredType)
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {

    ErrorResponse body = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Malformed JSON")
        .message("Не удалось прочитать тело запроса (некорректный JSON)")
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(
      Exception ex, HttpServletRequest request) {

    log.error("Unhandled exception at {}: ", request.getRequestURI(), ex);

    ErrorResponse body = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("Internal Server Error")
        .message("Произошла внутренняя ошибка сервера")
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

  private ValidationErrorDetail toValidationDetail(FieldError fieldError) {
    return ValidationErrorDetail.builder()
        .field(fieldError.getField())
        .rejectedValue(fieldError.getRejectedValue())
        .message(fieldError.getDefaultMessage())
        .build();
  }

  private ValidationErrorDetail toValidationDetail(ConstraintViolation<?> violation) {
    return ValidationErrorDetail.builder()
        .field(violation.getPropertyPath().toString())
        .rejectedValue(violation.getInvalidValue())
        .message(violation.getMessage())
        .build();
  }
}