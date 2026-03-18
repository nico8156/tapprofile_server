package com.nm.tapprofile.tapProfileContext.domain.model;

import java.time.Instant;
import java.util.Objects;

public final class Lead {

	private final LeadId id;
	private final ProfileId profileId;
	private final FirstName firstName;
	private final EmailAddress emailAddress;
	private final LeadMessage message;
	private final Instant createdAt;

	public Lead(
			LeadId id,
			ProfileId profileId,
			FirstName firstName,
			EmailAddress emailAddress,
			LeadMessage message,
			Instant createdAt) {
		this.id = Objects.requireNonNull(id);
		this.profileId = Objects.requireNonNull(profileId);
		this.firstName = Objects.requireNonNull(firstName);
		this.emailAddress = Objects.requireNonNull(emailAddress);
		this.message = Objects.requireNonNull(message);
		this.createdAt = Objects.requireNonNull(createdAt);
	}

	public LeadId id() {
		return id;
	}

	public ProfileId profileId() {
		return profileId;
	}

	public FirstName firstName() {
		return firstName;
	}

	public EmailAddress emailAddress() {
		return emailAddress;
	}

	public LeadMessage message() {
		return message;
	}

	public Instant createdAt() {
		return createdAt;
	}
}
