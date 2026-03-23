package com.nm.tapprofile.tapProfileContext.domain.model;

import java.util.UUID;

public record BadgeId(UUID value) {
	public static BadgeId newId() {
		return new BadgeId(UUID.randomUUID());
	}
}
