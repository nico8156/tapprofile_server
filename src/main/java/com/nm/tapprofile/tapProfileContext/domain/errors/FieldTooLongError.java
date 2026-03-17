package com.nm.tapprofile.tapProfileContext.domain.errors;

public record FieldTooLongError(String field, int maxLength) implements ValidationError {
	@Override
	public String code() {
		return "field.too_long";
	}

	@Override
	public String message() {
		return "Field '%s' must not exceed %d characters".formatted(field, maxLength);
	}
}
