package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto;

import java.time.Instant;
import java.util.UUID;

public record GetConnectionsHttpResponse(
		UUID profileId,
		String displayName,
		String headline,
		String role,
		Instant createdAt) {
}
