package com.nm.tapprofile.tapProfileContext.application.queries;

import java.time.Instant;
import java.util.UUID;

public record GetPublicProfileResult(
		UUID profileId,
		String slug,
		String displayName,
		String role,
		String headline,
		String bio,
		Instant publishedAt) {
}
