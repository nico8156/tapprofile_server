package com.nm.tapprofile.tapProfileContext.application.ports;

import com.nm.tapprofile.tapProfileContext.domain.model.Lead;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;

import java.util.List;

public interface LeadRepository {
	void save(Lead lead);

	List<Lead> findByProfileId(ProfileId profileId);
}
