package com.jetsys.northwindapiserver.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(err -> {
			String field = err instanceof FieldError ? ((FieldError) err).getField() : err.getObjectName();
			String msg = err.getDefaultMessage();
			errors.put(field, msg);
		});
		return new ResponseEntity<>(errors, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
		Map<String, String> body = new HashMap<>();
		body.put("error", ex.getMessage());
		return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> handleNotFound(EntityNotFoundException ex) {
		Map<String, String> body = new HashMap<>();
		body.put("error", ex.getMessage());
		return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Object> handleIllegalState(IllegalStateException ex) {
		Map<String, String> body = new HashMap<>();
		body.put("error", ex.getMessage());
		return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.CONFLICT);
	}
}
