package com.nm.tapprofile.tapProfileContext.domain.errors;

public record BadgeNotFoundError(String badgeToken) implements DomainError {
	@Override
	public String code() {
		return "badge.not_found";
	}

	@Override
	public String message() {
		return "Badge '%s' was not found".formatted(badgeToken);
	}
}
