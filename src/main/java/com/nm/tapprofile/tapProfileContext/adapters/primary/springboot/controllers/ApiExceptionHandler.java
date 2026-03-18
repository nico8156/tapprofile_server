package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.ErrorHttpResponse;
import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.ErrorItemHttpResponse;
import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileAlreadyPublishedError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotPublishedError;
import com.nm.tapprofile.tapProfileContext.domain.errors.SlugAlreadyTakenError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public final class ApiExceptionHandler {

	private ApiExceptionHandler() {
	}

	public static ResponseEntity<ErrorHttpResponse> toErrorResponse(DomainError error) {
		return toErrorResponse(List.of(error), mapStatus(error));
	}

	public static ResponseEntity<ErrorHttpResponse> toErrorResponse(List<DomainError> errors) {
		HttpStatus status = errors.isEmpty() ? HttpStatus.BAD_REQUEST : mapStatus(errors.get(0));
		return toErrorResponse(errors, status);
	}

	public static ResponseEntity<ErrorHttpResponse> toErrorResponse(List<DomainError> errors, HttpStatus status) {
		return ResponseEntity.status(status).body(
				new ErrorHttpResponse(
						errors.stream()
								.map(ApiExceptionHandler::toItem)
								.toList()));
	}

	private static ErrorItemHttpResponse toItem(DomainError error) {
		String field = error instanceof ValidationError validationError
				? validationError.field()
				: null;

		return new ErrorItemHttpResponse(
				error.code(),
				error.message(),
				field);
	}

	private static HttpStatus mapStatus(DomainError error) {
		return switch (error) {
			case ProfileNotFoundError ignored -> HttpStatus.NOT_FOUND;
			case ProfileNotPublishedError ignored -> HttpStatus.NOT_FOUND;
			case SlugAlreadyTakenError ignored -> HttpStatus.CONFLICT;
			case ProfileAlreadyPublishedError ignored -> HttpStatus.CONFLICT;
			case ValidationError ignored -> HttpStatus.BAD_REQUEST;
		};
	}
}
