package com.nm.tapprofile.tapProfileContext.domain.model;

import java.util.UUID;

public record ProfileViewId(UUID value) {
	public static ProfileViewId newId() {
		return new ProfileViewId(UUID.randomUUID());
	}
}
