package com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem;

import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.domain.model.Profile;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.domain.model.Slug;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class InMemoryProfileRepository implements ProfileRepository {

	private final Map<String, Profile> profilesBySlug = new HashMap<>();
	private final Map<UUID, Profile> profilesById = new HashMap<>();

	@Override
	public boolean existsBySlug(Slug slug) {
		return profilesBySlug.containsKey(slug.value());
	}

	@Override
	public void save(Profile profile) {
		profilesBySlug.put(profile.slug().value(), profile);
		profilesById.put(profile.id().value(), profile);
	}

	@Override
	public Optional<Profile> findBySlug(Slug slug) {
		return Optional.ofNullable(profilesBySlug.get(slug.value()));
	}

	@Override
	public Optional<Profile> findById(ProfileId profileId) {
		return Optional.ofNullable(profilesById.get(profileId.value()));
	}
}
