package com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem;

import com.nm.tapprofile.tapProfileContext.application.ports.MagicLinkRepository;
import com.nm.tapprofile.tapProfileContext.domain.model.MagicLink;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class InMemoryMagicLinkRepository implements MagicLinkRepository {

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
}
