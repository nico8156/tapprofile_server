package com.nm.tapprofile.tapProfileContext.application.queryhandlers;

import com.nm.tapprofile.tapProfileContext.application.ports.LeadRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.application.queries.GetDashboardQuery;
import com.nm.tapprofile.tapProfileContext.application.queries.GetDashboardResult;
import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.shared.result.Result;

import java.util.Comparator;

public final class GetDashboardQueryHandler {

	private final ProfileRepository profileRepository;
	private final LeadRepository leadRepository;

	public GetDashboardQueryHandler(
			ProfileRepository profileRepository,
			LeadRepository leadRepository) {
		this.profileRepository = profileRepository;
		this.leadRepository = leadRepository;
	}

	public Result<DomainError, GetDashboardResult> handle(GetDashboardQuery query) {
		ProfileId profileId = new ProfileId(query.profileId());

		var maybeProfile = profileRepository.findById(profileId);

		if (maybeProfile.isEmpty()) {
			return Result.failure(new ProfileNotFoundError(query.profileId().toString()));
		}

		var profile = maybeProfile.get();
		var leads = leadRepository.findByProfileId(profileId).stream()
				.sorted(Comparator.comparing(lead -> lead.createdAt(), Comparator.reverseOrder()))
				.toList();

		var leadItems = leads.stream()
				.map(lead -> new GetDashboardResult.LeadItem(
						lead.id().value(),
						lead.firstName().value(),
						lead.emailAddress().value(),
						lead.message().value(),
						lead.createdAt()))
				.toList();

		return Result.success(new GetDashboardResult(
				new GetDashboardResult.ProfileSummary(
						profile.id().value(),
						profile.slug().value(),
						profile.displayName().value(),
						profile.status().name()),
				new GetDashboardResult.Metrics(
						leadItems.size()),
				leadItems));
	}
}
