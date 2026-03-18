package com.nm.tapprofile.tapProfileContext.application.ports;

import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileView;

import java.util.List;

public interface ProfileViewRepository {
	void save(ProfileView profileView);

	List<ProfileView> findByProfileId(ProfileId profileId);
}
