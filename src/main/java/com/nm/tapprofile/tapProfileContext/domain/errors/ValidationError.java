package com.nm.tapprofile.tapProfileContext.domain.errors;

public sealed interface ValidationError extends DomainError
		permits FieldBlankError, FieldTooLongError, InvalidSlugError, InvalidEmailError {
	String field();
}
