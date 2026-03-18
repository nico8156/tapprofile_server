package com.nm.tapprofile.tapProfileContext.application.commandhandlers;

import com.nm.tapprofile.tapProfileContext.application.commands.RegisterProfileViewCommand;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileViewRepository;
import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotPublishedError;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileStatus;
import com.nm.tapprofile.tapProfileContext.domain.model.Slug;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileViewFactory;
import com.nm.tapprofile.tapProfileContext.shared.result.Result;

public final class RegisterProfileViewCommandHandler {

	private final ProfileRepository profileRepository;
	private final ProfileViewRepository profileViewRepository;
	private final ProfileViewFactory profileViewFactory;

	public RegisterProfileViewCommandHandler(
			ProfileRepository profileRepository,
			ProfileViewRepository profileViewRepository,
			ProfileViewFactory profileViewFactory) {
		this.profileRepository = profileRepository;
		this.profileViewRepository = profileViewRepository;
		this.profileViewFactory = profileViewFactory;
	}

	public Result<DomainError, com.nm.tapprofile.tapProfileContext.domain.model.ProfileViewId> handle(
			RegisterProfileViewCommand command) {
		var slugValidation = Slug.create(command.slug());

		if (slugValidation.isInvalid()) {
			return Result.failure(new ProfileNotFoundError(command.slug()));
		}

		var slug = slugValidation.get();
		var maybeProfile = profileRepository.findBySlug(slug);

		if (maybeProfile.isEmpty()) {
			return Result.failure(new ProfileNotFoundError(command.slug()));
		}

		var profile = maybeProfile.get();

		if (profile.status() != ProfileStatus.PUBLISHED) {
			return Result.failure(new ProfileNotPublishedError(profile.slug().value()));
		}

		var profileView = profileViewFactory.create(profile.id());
		profileViewRepository.save(profileView);

		return Result.success(profileView.id());
	}
}
