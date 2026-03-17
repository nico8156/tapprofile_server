package com.nm.tapprofile.tapProfileContext.domain.errors;

public record InvalidSlugError(String field) implements ValidationError {
	@Override
	public String code() {
		return "slug.invalid";
	}

	@Override
	public String message() {
		return "Field '%s' must contain only lowercase letters, digits or hyphens".formatted(field);
	}
}
