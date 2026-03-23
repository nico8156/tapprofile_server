package com.nm.tapprofile.tapProfileContext.application.queryhandlers;

import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.application.queries.GetPublicProfileQuery;
import com.nm.tapprofile.tapProfileContext.application.queries.GetPublicProfileResult;
import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotPublishedError;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileStatus;
import com.nm.tapprofile.tapProfileContext.domain.model.Slug;
import com.nm.tapprofile.tapProfileContext.shared.result.Result;

public final class GetPublicProfileQueryHandler {

	private final ProfileRepository profileRepository;

	public GetPublicProfileQueryHandler(ProfileRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	public Result<DomainError, GetPublicProfileResult> handle(GetPublicProfileQuery query) {
		var slugValidation = Slug.create(query.slug());

		if (slugValidation.isInvalid()) {
			return Result.failure(new ProfileNotFoundError(null));
		}

		Slug slug = slugValidation.get();

		var maybeProfile = profileRepository.findBySlug(slug);

		if (maybeProfile.isEmpty()) {
			return Result.failure(new ProfileNotFoundError(query.slug()));
		}

		var profile = maybeProfile.get();

		if (profile.status() != ProfileStatus.PUBLISHED) {
			return Result.failure(new ProfileNotPublishedError(profile.slug().value()));
		}

		return Result.success(new GetPublicProfileResult(
				profile.id().value(),
				profile.slug().value(),
				profile.displayName().value(),
				profile.role().name(),
				profile.headline().value(),
				profile.bio().value(),
				profile.publishedAt()));
	}
}
