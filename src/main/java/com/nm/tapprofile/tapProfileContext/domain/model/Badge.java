package com.nm.tapprofile.tapProfileContext.domain.model;

import java.time.Instant;
import java.util.Objects;

public final class Badge {

	private final BadgeId id;
	private final ProfileId profileId;
	private final String badgeToken;
	private final BadgeStatus status;
	private final Instant createdAt;

	public Badge(
			BadgeId id,
			ProfileId profileId,
			String badgeToken,
			BadgeStatus status,
			Instant createdAt) {
		this.id = Objects.requireNonNull(id);
		this.profileId = Objects.requireNonNull(profileId);
		this.badgeToken = Objects.requireNonNull(badgeToken);
		this.status = Objects.requireNonNull(status);
		this.createdAt = Objects.requireNonNull(createdAt);
	}

	public BadgeId id() {
		return id;
	}

	public ProfileId profileId() {
		return profileId;
	}

	public String badgeToken() {
		return badgeToken;
	}

	public BadgeStatus status() {
		return status;
	}

	public Instant createdAt() {
		return createdAt;
	}
}
