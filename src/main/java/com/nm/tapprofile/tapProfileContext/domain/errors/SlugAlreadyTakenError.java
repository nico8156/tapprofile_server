package com.nm.tapprofile.tapProfileContext.domain.errors;

public record SlugAlreadyTakenError(String slug) implements DomainError {
	@Override
	public String code() {
		return "slug.already_taken";
	}

	@Override
	public String message() {
		return "Slug '%s' is already taken".formatted(slug);
	}
}
