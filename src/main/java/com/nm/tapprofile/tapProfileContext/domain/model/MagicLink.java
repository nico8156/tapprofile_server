package com.nm.tapprofile.tapProfileContext.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class MagicLink {

	private final UUID token;
	private final ProfileId profileId;
	private final Instant expiresAt;
	private final Instant createdAt;
	private final Instant usedAt;

	public MagicLink(
			UUID token,
			ProfileId profileId,
			Instant expiresAt,
			Instant createdAt,
			Instant usedAt) {
		this.token = Objects.requireNonNull(token);
		this.profileId = Objects.requireNonNull(profileId);
		this.expiresAt = Objects.requireNonNull(expiresAt);
		this.createdAt = Objects.requireNonNull(createdAt);
		this.usedAt = usedAt;
	}

	public UUID token() {
		return token;
	}

	public ProfileId profileId() {
		return profileId;
	}

	public Instant expiresAt() {
		return expiresAt;
	}

	public Instant createdAt() {
		return createdAt;
	}

	public Instant usedAt() {
		return usedAt;
	}

	public boolean isExpiredAt(Instant instant) {
		return !expiresAt.isAfter(instant);
	}

	public MagicLink markUsedAt(Instant instant) {
		return new MagicLink(token, profileId, expiresAt, createdAt, instant);
	}
}
