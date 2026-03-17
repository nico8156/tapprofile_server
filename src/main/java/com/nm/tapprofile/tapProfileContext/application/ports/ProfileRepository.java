package com.nm.tapprofile.tapProfileContext.application.ports;

import com.nm.tapprofile.tapProfileContext.domain.model.Profile;
import com.nm.tapprofile.tapProfileContext.domain.model.Slug;

import java.util.Optional;

public interface ProfileRepository {
	boolean existsBySlug(Slug slug);

	void save(Profile profile);

	Optional<Profile> findBySlug(Slug slug);
}
