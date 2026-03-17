package com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem;

import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.domain.model.Profile;
import com.nm.tapprofile.tapProfileContext.domain.model.Slug;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class InMemoryProfileRepository implements ProfileRepository {

	private final Map<String, Profile> store = new HashMap<>();

	@Override
	public boolean existsBySlug(Slug slug) {
		return store.containsKey(slug.value());
	}

	@Override
	public void save(Profile profile) {
		store.put(profile.slug().value(), profile);
	}

	@Override
	public Optional<Profile> findBySlug(Slug slug) {
		return Optional.ofNullable(store.get(slug.value()));
	}
}
