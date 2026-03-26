package com.nm.tapprofile.tapProfileContext.application.commandhandlers;

import com.nm.tapprofile.tapProfileContext.application.commands.CreateProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.ports.BadgeRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.MagicLinkEmailSender;
import com.nm.tapprofile.tapProfileContext.application.ports.MagicLinkRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.SlugAlreadyTakenError;
import com.nm.tapprofile.tapProfileContext.domain.model.Badge;
import com.nm.tapprofile.tapProfileContext.domain.model.MagicLink;
import com.nm.tapprofile.tapProfileContext.domain.model.Profile;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.domain.services.BadgeFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.MagicLinkFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileFactory;
import com.nm.tapprofile.tapProfileContext.shared.result.Result;
import com.nm.tapprofile.tapProfileContext.shared.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class CreateProfileCommandHandler {

	private static final Logger log = LoggerFactory.getLogger(CreateProfileCommandHandler.class);

	private final ProfileRepository profileRepository;
	private final BadgeRepository badgeRepository;
	private final MagicLinkRepository magicLinkRepository;
	private final MagicLinkFactory magicLinkFactory;
	private final MagicLinkEmailSender magicLinkEmailSender;
	private final ProfileFactory profileFactory;
	private final BadgeFactory badgeFactory;

	public CreateProfileCommandHandler(
			ProfileRepository profileRepository,
			BadgeRepository badgeRepository,
			MagicLinkRepository magicLinkRepository,
			MagicLinkFactory magicLinkFactory,
			MagicLinkEmailSender magicLinkEmailSender,
			ProfileFactory profileFactory,
			BadgeFactory badgeFactory) {
		this.profileRepository = profileRepository;
		this.badgeRepository = badgeRepository;
		this.magicLinkRepository = magicLinkRepository;
		this.magicLinkFactory = magicLinkFactory;
		this.magicLinkEmailSender = magicLinkEmailSender;
		this.profileFactory = profileFactory;
		this.badgeFactory = badgeFactory;
	}

	public Result<List<DomainError>, ProfileId> handle(CreateProfileCommand command) {
		log.info("Creating profile: displayName={}, email={}", command.displayName(), command.email());

		Validation<com.nm.tapprofile.tapProfileContext.domain.errors.ValidationError, Profile> validation = profileFactory
				.createDraft(
						command.slug(),
						command.displayName(),
						command.email(),
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
		log.info("Profile created: id={}, email={}", profile.id().value(), profile.emailAddress().value());

		badgeRepository.save(createUniqueBadge(profile.id()));
		MagicLink magicLink = createUniqueMagicLink(profile.id());
		log.info("Magic link generated: profileId={}, token={}", profile.id().value(), magicLink.token());
		log.info("Magic link URL: http://localhost:3000/magic-link/{}", magicLink.token());
		magicLinkRepository.save(magicLink);
		magicLinkEmailSender.sendMagicLink(profile.emailAddress(), magicLink.token());
		return Result.success(profile.id());
	}

	private Badge createUniqueBadge(ProfileId profileId) {
		Badge badge = badgeFactory.create(profileId);

		while (badgeRepository.existsByBadgeToken(badge.badgeToken())) {
			badge = badgeFactory.create(profileId);
		}

		return badge;
	}

	private MagicLink createUniqueMagicLink(ProfileId profileId) {
		MagicLink magicLink = magicLinkFactory.create(profileId);

		while (magicLinkRepository.existsByToken(magicLink.token())) {
			magicLink = magicLinkFactory.create(profileId);
		}

		return magicLink;
	}
}
