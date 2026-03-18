package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto;

import java.time.Instant;
import java.util.UUID;

public record PublicProfileHttpResponse(
		UUID profileId,
		String slug,
		String displayName,
		String headline,
		String bio,
		Instant publishedAt) {
}
