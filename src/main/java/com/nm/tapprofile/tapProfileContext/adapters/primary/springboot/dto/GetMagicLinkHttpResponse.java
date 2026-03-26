package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetMagicLinkHttpResponse(
		ProfileItem profile,
		List<ContactItem> contacts) {
	public record ProfileItem(
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
