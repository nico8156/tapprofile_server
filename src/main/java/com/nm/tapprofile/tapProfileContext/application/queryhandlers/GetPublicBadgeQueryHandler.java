package com.nm.tapprofile.tapProfileContext.application.queryhandlers;

import com.nm.tapprofile.tapProfileContext.application.ports.BadgeRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.application.queries.GetPublicBadgeQuery;
import com.nm.tapprofile.tapProfileContext.application.queries.GetPublicBadgeResult;
import com.nm.tapprofile.tapProfileContext.domain.errors.BadgeNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.BadgeRevokedError;
import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotPublishedError;
import com.nm.tapprofile.tapProfileContext.domain.model.BadgeStatus;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileStatus;
import com.nm.tapprofile.tapProfileContext.shared.result.Result;

public final class GetPublicBadgeQueryHandler {

	private final BadgeRepository badgeRepository;
	private final ProfileRepository profileRepository;

	public GetPublicBadgeQueryHandler(
			BadgeRepository badgeRepository,
			ProfileRepository profileRepository) {
		this.badgeRepository = badgeRepository;
		this.profileRepository = profileRepository;
	}

	public Result<DomainError, GetPublicBadgeResult> handle(GetPublicBadgeQuery query) {
		var maybeBadge = badgeRepository.findByBadgeToken(query.badgeToken());

		if (maybeBadge.isEmpty()) {
			return Result.failure(new BadgeNotFoundError(query.badgeToken()));
		}

		var badge = maybeBadge.get();

		if (badge.status() == BadgeStatus.REVOKED) {
			return Result.failure(new BadgeRevokedError(query.badgeToken()));
		}

		var maybeProfile = profileRepository.findById(badge.profileId());

		if (maybeProfile.isEmpty()) {
			return Result.failure(new ProfileNotFoundError(badge.profileId().value().toString()));
		}

		var profile = maybeProfile.get();

		if (profile.status() != ProfileStatus.PUBLISHED) {
			return Result.failure(new ProfileNotPublishedError(profile.slug().value()));
		}

		return Result.success(new GetPublicBadgeResult(
				profile.slug().value(),
				profile.displayName().value(),
				profile.role().name(),
				profile.headline().value(),
				profile.bio().value(),
				profile.publishedAt(),
				new GetPublicBadgeResult.BadgeSummary(
						badge.badgeToken(),
						badge.status().name(),
						badge.createdAt())));
	}
}
