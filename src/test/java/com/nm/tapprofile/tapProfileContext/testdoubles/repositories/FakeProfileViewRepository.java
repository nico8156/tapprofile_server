package com.nm.tapprofile.tapProfileContext.testdoubles.repositories;

import com.nm.tapprofile.tapProfileContext.application.ports.ProfileViewRepository;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileView;

import java.util.ArrayList;
import java.util.List;

public final class FakeProfileViewRepository implements ProfileViewRepository {

	private final List<ProfileView> profileViews = new ArrayList<>();

	@Override
	public void save(ProfileView profileView) {
		profileViews.add(profileView);
	}

	@Override
	public List<ProfileView> findByProfileId(ProfileId profileId) {
		return profileViews.stream()
				.filter(profileView -> profileView.profileId().equals(profileId))
				.toList();
	}
}
