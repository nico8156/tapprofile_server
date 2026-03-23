package com.nm.tapprofile.tapProfileContext.application.commandhandlers;

import com.nm.tapprofile.tapProfileContext.application.commands.CreateProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.ports.BadgeRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.SlugAlreadyTakenError;
import com.nm.tapprofile.tapProfileContext.domain.model.Badge;
import com.nm.tapprofile.tapProfileContext.domain.model.Profile;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.domain.services.BadgeFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileFactory;
import com.nm.tapprofile.tapProfileContext.shared.result.Result;
import com.nm.tapprofile.tapProfileContext.shared.validation.Validation;

import java.util.List;

public final class CreateProfileCommandHandler {

	private final ProfileRepository profileRepository;
	private final BadgeRepository badgeRepository;
	private final ProfileFactory profileFactory;
	private final BadgeFactory badgeFactory;

	public CreateProfileCommandHandler(
			ProfileRepository profileRepository,
			BadgeRepository badgeRepository,
			ProfileFactory profileFactory,
			BadgeFactory badgeFactory) {
		this.profileRepository = profileRepository;
		this.badgeRepository = badgeRepository;
		this.profileFactory = profileFactory;
		this.badgeFactory = badgeFactory;
	}

	public Result<List<DomainError>, ProfileId> handle(CreateProfileCommand command) {
		Validation<com.nm.tapprofile.tapProfileContext.domain.errors.ValidationError, Profile> validation = profileFactory
				.createDraft(
						command.slug(),
						command.displayName(),
						command.role(),
						command.headline(),
						command.bio());

		if (validation.isInvalid()) {
			return Result.failure(List.copyOf(validation.getErrors()));
		}

		Profile profile = validation.get();

		if (profileRepository.existsBySlug(profile.slug())) {
			return Result.failure(List.of(new SlugAlreadyTakenError(profile.slug().value())));
		}

		profileRepository.save(profile);
		badgeRepository.save(createUniqueBadge(profile.id()));
		return Result.success(profile.id());
	}

	private Badge createUniqueBadge(ProfileId profileId) {
		Badge badge = badgeFactory.create(profileId);

		while (badgeRepository.existsByBadgeToken(badge.badgeToken())) {
			badge = badgeFactory.create(profileId);
		}

		return badge;
	}
}
