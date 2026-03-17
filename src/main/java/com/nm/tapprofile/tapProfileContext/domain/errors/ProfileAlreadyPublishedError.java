package com.nm.tapprofile.tapProfileContext.domain.errors;

import java.util.UUID;

public record ProfileAlreadyPublishedError(UUID profileId) implements DomainError {
	@Override
	public String code() {
		return "profile.already_published";
	}

	@Override
	public String message() {
		return "Profile '%s' is already published".formatted(profileId);
	}
}
