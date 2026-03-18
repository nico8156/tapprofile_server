package com.nm.tapprofile.tapProfileContext.domain.errors;

public record InvalidEmailError(String field) implements ValidationError {
	@Override
	public String code() {
		return "email.invalid";
	}

	@Override
	public String message() {
		return "Field '%s' must be a valid email address".formatted(field);
	}
}
