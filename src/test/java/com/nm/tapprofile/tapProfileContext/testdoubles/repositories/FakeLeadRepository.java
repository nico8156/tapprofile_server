package com.nm.tapprofile.tapProfileContext.testdoubles.repositories;

import com.nm.tapprofile.tapProfileContext.application.ports.LeadRepository;
import com.nm.tapprofile.tapProfileContext.domain.model.Lead;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;

import java.util.ArrayList;
import java.util.List;

public final class FakeLeadRepository implements LeadRepository {

	private final List<Lead> leads = new ArrayList<>();

	@Override
	public void save(Lead lead) {
		leads.add(lead);
	}

	@Override
	public List<Lead> findByProfileId(ProfileId profileId) {
		return leads.stream()
				.filter(lead -> lead.profileId().equals(profileId))
				.toList();
	}
}
