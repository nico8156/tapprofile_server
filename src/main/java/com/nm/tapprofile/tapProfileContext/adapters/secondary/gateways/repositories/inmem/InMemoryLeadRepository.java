package com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem;

import com.nm.tapprofile.tapProfileContext.application.ports.LeadRepository;
import com.nm.tapprofile.tapProfileContext.domain.model.Lead;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;

import java.util.ArrayList;
import java.util.List;

public final class InMemoryLeadRepository implements LeadRepository {

	private final List<Lead> store = new ArrayList<>();

	@Override
	public void save(Lead lead) {
		store.add(lead);
	}

	@Override
	public List<Lead> findByProfileId(ProfileId profileId) {
		return store.stream()
				.filter(lead -> lead.profileId().equals(profileId))
				.toList();
	}
}
