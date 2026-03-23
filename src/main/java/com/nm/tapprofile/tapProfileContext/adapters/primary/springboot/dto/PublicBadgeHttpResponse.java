package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto;

import java.time.Instant;

public record PublicBadgeHttpResponse(
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
