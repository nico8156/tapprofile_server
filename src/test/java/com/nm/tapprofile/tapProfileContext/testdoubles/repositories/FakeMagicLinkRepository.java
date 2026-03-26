package com.nm.tapprofile.tapProfileContext.testdoubles.repositories;

import com.nm.tapprofile.tapProfileContext.application.ports.MagicLinkRepository;
import com.nm.tapprofile.tapProfileContext.domain.model.MagicLink;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class FakeMagicLinkRepository implements MagicLinkRepository {

	private final Map<UUID, MagicLink> magicLinksByToken = new HashMap<>();

	@Override
	public boolean existsByToken(UUID token) {
		return magicLinksByToken.containsKey(token);
	}

	@Override
	public void save(MagicLink magicLink) {
		magicLinksByToken.put(magicLink.token(), magicLink);
	}

	@Override
	public Optional<MagicLink> findByToken(UUID token) {
		return Optional.ofNullable(magicLinksByToken.get(token));
	}

	public Optional<MagicLink> findByProfileId(ProfileId profileId) {
		return magicLinksByToken.values().stream()
				.filter(magicLink -> magicLink.profileId().equals(profileId))
				.findFirst();
	}
}
