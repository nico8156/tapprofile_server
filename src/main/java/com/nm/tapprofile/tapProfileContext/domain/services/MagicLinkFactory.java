package com.nm.tapprofile.tapProfileContext.domain.services;

import com.nm.tapprofile.tapProfileContext.domain.model.MagicLink;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.shared.time.DateTimeProvider;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public final class MagicLinkFactory {

	private final DateTimeProvider dateTimeProvider;
	private final Duration ttl;

	public MagicLinkFactory(DateTimeProvider dateTimeProvider, Duration ttl) {
		this.dateTimeProvider = dateTimeProvider;
		this.ttl = ttl;
	}

	public MagicLink create(ProfileId profileId) {
		Instant createdAt = dateTimeProvider.now();

		return new MagicLink(
				UUID.randomUUID(),
				profileId,
				createdAt.plus(ttl),
				createdAt,
				null);
	}
}
