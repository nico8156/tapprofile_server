package com.nm.tapprofile.tapProfileContext.application.ports;

import com.nm.tapprofile.tapProfileContext.domain.model.Connection;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository {
	void save(Connection connection);

	List<Connection> findByProfileId(ProfileId profileId);

	Optional<Connection> findByProfileIds(ProfileId firstProfileId, ProfileId secondProfileId);
}
