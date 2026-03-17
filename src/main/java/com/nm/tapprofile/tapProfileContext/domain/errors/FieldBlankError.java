package com.nm.tapprofile.tapProfileContext.domain.errors;

public record FieldBlankError(String field) implements ValidationError {
	@Override
	public String code() {
		return "field.blank";
	}

	@Override
	public String message() {
		return "Field '%s' must not be blank".formatted(field);
	}
}
