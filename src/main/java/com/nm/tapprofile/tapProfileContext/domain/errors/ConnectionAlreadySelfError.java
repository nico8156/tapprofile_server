package com.nm.tapprofile.tapProfileContext.domain.errors;

import java.util.UUID;

public record ConnectionAlreadySelfError(UUID profileId) implements DomainError {
	@Override
	public String code() {
		return "connection.self_not_allowed";
	}

	@Override
	public String message() {
		return "Profile '%s' cannot connect to itself".formatted(profileId);
	}
}
