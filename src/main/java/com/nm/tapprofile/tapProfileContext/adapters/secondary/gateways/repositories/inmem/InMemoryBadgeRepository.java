package com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem;

import com.nm.tapprofile.tapProfileContext.application.ports.BadgeRepository;
import com.nm.tapprofile.tapProfileContext.domain.model.Badge;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class InMemoryBadgeRepository implements BadgeRepository {

	private final Map<String, Badge> badgesByToken = new HashMap<>();
	private final Map<UUID, Badge> badgesByProfileId = new HashMap<>();

	@Override
	public void save(Badge badge) {
		badgesByToken.put(badge.badgeToken(), badge);
		badgesByProfileId.put(badge.profileId().value(), badge);
	}

	@Override
	public Optional<Badge> findByBadgeToken(String badgeToken) {
		return Optional.ofNullable(badgesByToken.get(badgeToken));
	}

	@Override
	public Optional<Badge> findByProfileId(ProfileId profileId) {
		return Optional.ofNullable(badgesByProfileId.get(profileId.value()));
	}

	@Override
	public boolean existsByBadgeToken(String badgeToken) {
		return badgesByToken.containsKey(badgeToken);
	}
}
