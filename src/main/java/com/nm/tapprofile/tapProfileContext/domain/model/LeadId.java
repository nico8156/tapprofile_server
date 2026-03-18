package com.nm.tapprofile.tapProfileContext.domain.model;

import java.util.UUID;

public record LeadId(UUID value) {
	public static LeadId newId() {
		return new LeadId(UUID.randomUUID());
	}
}
