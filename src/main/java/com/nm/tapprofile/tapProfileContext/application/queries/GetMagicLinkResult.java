package com.nm.tapprofile.tapProfileContext.application.queries;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetMagicLinkResult(
		ProfileSummary profile,
		List<ContactItem> contacts) {
	public record ProfileSummary(
			UUID profileId,
			String slug,
			String displayName,
			String email,
			String role,
			String status) {
	}

	public record ContactItem(
			UUID profileId,
			String displayName,
			String headline,
			String role,
			Instant createdAt) {
	}
}
