package com.nm.tapprofile.tapProfileContext.domain.services;

import com.nm.tapprofile.tapProfileContext.domain.model.Badge;
import com.nm.tapprofile.tapProfileContext.domain.model.BadgeId;
import com.nm.tapprofile.tapProfileContext.domain.model.BadgeStatus;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.shared.time.DateTimeProvider;

import java.util.UUID;

public final class BadgeFactory {

	private final DateTimeProvider dateTimeProvider;

	public BadgeFactory(DateTimeProvider dateTimeProvider) {
		this.dateTimeProvider = dateTimeProvider;
	}

	public Badge create(ProfileId profileId) {
		return new Badge(
				BadgeId.newId(),
				profileId,
				UUID.randomUUID().toString(),
				BadgeStatus.ACTIVE,
				dateTimeProvider.now());
	}
}
