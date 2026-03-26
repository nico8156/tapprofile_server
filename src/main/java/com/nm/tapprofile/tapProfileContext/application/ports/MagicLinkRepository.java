package com.nm.tapprofile.tapProfileContext.application.ports;

import com.nm.tapprofile.tapProfileContext.domain.model.MagicLink;

import java.util.Optional;
import java.util.UUID;

public interface MagicLinkRepository {
	boolean existsByToken(UUID token);

	void save(MagicLink magicLink);

	Optional<MagicLink> findByToken(UUID token);
}
