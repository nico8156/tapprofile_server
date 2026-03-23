package com.nm.tapprofile.tapProfileContext.domain.errors;

public sealed interface DomainError permits
		ValidationError,
		SlugAlreadyTakenError,
		ProfileNotFoundError,
		ProfileAlreadyPublishedError,
		ProfileNotPublishedError,
		BadgeNotFoundError,
		BadgeRevokedError,
		ConnectionAlreadySelfError {
	String code();

	String message();
}
