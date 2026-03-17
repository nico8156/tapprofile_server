package com.nm.tapprofile.tapProfileContext.domain.errors;

public sealed interface DomainError permits
		ValidationError,
		SlugAlreadyTakenError,
		ProfileNotFoundError,
		ProfileAlreadyPublishedError,
		ProfileNotPublishedError {
	String code();

	String message();
}
