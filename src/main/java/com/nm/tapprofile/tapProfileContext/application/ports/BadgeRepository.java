package com.nm.tapprofile.tapProfileContext.application.ports;

import com.nm.tapprofile.tapProfileContext.domain.model.Badge;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;

import java.util.Optional;

public interface BadgeRepository {
	void save(Badge badge);

	Optional<Badge> findByBadgeToken(String badgeToken);

	Optional<Badge> findByProfileId(ProfileId profileId);

	boolean existsByBadgeToken(String badgeToken);
}
