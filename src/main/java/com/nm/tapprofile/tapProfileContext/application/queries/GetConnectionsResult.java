package com.nm.tapprofile.tapProfileContext.application.queries;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetConnectionsResult(List<Item> connections) {
	public record Item(
			UUID profileId,
			String displayName,
			String headline,
			String role,
			Instant createdAt) {
	}
}
