package com.nm.tapprofile.tapProfileContext.domain.errors;

public record MagicLinkExpiredError(String token) implements DomainError {

	@Override
	public String code() {
		return "magic_link.expired";
	}

	@Override
	public String message() {
		return "Magic link expired: " + token;
	}
}
