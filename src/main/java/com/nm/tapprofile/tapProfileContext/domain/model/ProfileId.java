package com.nm.tapprofile.tapProfileContext.domain.model;

import java.util.UUID;

public record ProfileId(UUID value) {
	public static ProfileId newId() {
		return new ProfileId(UUID.randomUUID());
	}
}
