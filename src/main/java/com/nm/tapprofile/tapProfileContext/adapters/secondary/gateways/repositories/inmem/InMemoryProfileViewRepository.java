package com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem;

import com.nm.tapprofile.tapProfileContext.application.ports.ProfileViewRepository;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileView;

import java.util.ArrayList;
import java.util.List;

public final class InMemoryProfileViewRepository implements ProfileViewRepository {

	private final List<ProfileView> store = new ArrayList<>();

	@Override
	public void save(ProfileView profileView) {
		store.add(profileView);
	}

	@Override
	public List<ProfileView> findByProfileId(ProfileId profileId) {
		return store.stream()
				.filter(profileView -> profileView.profileId().equals(profileId))
				.toList();
	}
}
