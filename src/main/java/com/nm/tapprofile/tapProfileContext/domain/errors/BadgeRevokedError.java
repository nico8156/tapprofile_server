package com.nm.tapprofile.tapProfileContext.domain.errors;

public record BadgeRevokedError(String badgeToken) implements DomainError {
	@Override
	public String code() {
		return "badge.revoked";
	}

	@Override
	public String message() {
		return "Badge '%s' is revoked".formatted(badgeToken);
	}
}
