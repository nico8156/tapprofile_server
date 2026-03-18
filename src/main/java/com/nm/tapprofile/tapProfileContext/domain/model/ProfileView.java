package com.nm.tapprofile.tapProfileContext.domain.model;

import java.time.Instant;
import java.util.Objects;

public final class ProfileView {

	private final ProfileViewId id;
	private final ProfileId profileId;
	private final Instant occurredAt;

	public ProfileView(
			ProfileViewId id,
			ProfileId profileId,
			Instant occurredAt) {
		this.id = Objects.requireNonNull(id);
		this.profileId = Objects.requireNonNull(profileId);
		this.occurredAt = Objects.requireNonNull(occurredAt);
	}

	public ProfileViewId id() {
		return id;
	}

	public ProfileId profileId() {
		return profileId;
	}

	public Instant occurredAt() {
		return occurredAt;
	}
}
