package com.nm.tapprofile.tapProfileContext.application.queries;

import java.time.Instant;

public record GetPublicBadgeResult(
		String slug,
		String displayName,
		String role,
		String headline,
		String bio,
		Instant publishedAt,
		BadgeSummary badge) {
	public record BadgeSummary(
			String badgeToken,
			String status,
			Instant createdAt) {
	}
}
