package com.nm.tapprofile.tapProfileContext.domain.errors;

public record ProfileNotPublishedError(String slug) implements DomainError {
	@Override
	public String code() {
		return "profile.not_published";
	}

	@Override
	public String message() {
		return "Profile '%s' is not published".formatted(slug);
	}
}
