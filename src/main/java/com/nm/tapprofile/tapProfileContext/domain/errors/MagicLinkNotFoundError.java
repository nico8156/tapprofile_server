package com.nm.tapprofile.tapProfileContext.domain.errors;

public record MagicLinkNotFoundError(String token) implements DomainError {

	@Override
	public String code() {
		return "magic_link.not_found";
	}

	@Override
	public String message() {
		return "Magic link not found: " + token;
	}
}
