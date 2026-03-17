package com.nm.tapprofile.tapProfileContext.domain.errors;

public sealed interface DomainError permits ValidationError, SlugAlreadyTakenError {
	String code();

	String message();
}
