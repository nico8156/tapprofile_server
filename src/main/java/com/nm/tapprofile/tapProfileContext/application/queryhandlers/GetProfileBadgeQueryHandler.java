package com.nm.tapprofile.tapProfileContext.application.queryhandlers;

import com.nm.tapprofile.tapProfileContext.application.ports.BadgeRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.application.queries.GetProfileBadgeQuery;
import com.nm.tapprofile.tapProfileContext.application.queries.GetProfileBadgeResult;
import com.nm.tapprofile.tapProfileContext.domain.errors.BadgeNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.shared.result.Result;

public final class GetProfileBadgeQueryHandler {

	private static final String PUBLIC_BADGE_BASE_URL = "http://localhost:3000/b/";

	private final ProfileRepository profileRepository;
	private final BadgeRepository badgeRepository;

	public GetProfileBadgeQueryHandler(
			ProfileRepository profileRepository,
			BadgeRepository badgeRepository) {
		this.profileRepository = profileRepository;
		this.badgeRepository = badgeRepository;
	}

	public Result<DomainError, GetProfileBadgeResult> handle(GetProfileBadgeQuery query) {
		var profileId = new ProfileId(query.profileId());
		var maybeProfile = profileRepository.findById(profileId);

		if (maybeProfile.isEmpty()) {
			return Result.failure(new ProfileNotFoundError(query.profileId().toString()));
		}

		var maybeBadge = badgeRepository.findByProfileId(profileId);

		if (maybeBadge.isEmpty()) {
			return Result.failure(new BadgeNotFoundError(query.profileId().toString()));
		}

		var badge = maybeBadge.get();

		return Result.success(new GetProfileBadgeResult(
				badge.badgeToken(),
				PUBLIC_BADGE_BASE_URL + badge.badgeToken()));
	}
}
