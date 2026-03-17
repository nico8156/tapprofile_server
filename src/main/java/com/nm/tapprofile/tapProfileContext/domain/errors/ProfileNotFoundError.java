package com.nm.tapprofile.tapProfileContext.domain.errors;

import java.util.UUID;

public record ProfileNotFoundError(UUID profileId) implements DomainError {
	@Override
	public String code() {
		return "profile.not_found";
	}

	@Override
	public String message() {
		return "Profile '%s' was not found".formatted(profileId);
	}
}
