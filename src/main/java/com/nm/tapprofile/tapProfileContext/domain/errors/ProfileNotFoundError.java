package com.nm.tapprofile.tapProfileContext.domain.errors;

public record ProfileNotFoundError(String identifier) implements DomainError {
	@Override
	public String code() {
		return "profile.not_found";
	}

	@Override
	public String message() {
		return "Profile '%s' was not found".formatted(identifier);
	}
}
