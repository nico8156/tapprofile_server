package com.nm.tapprofile.tapProfileContext.application.commandhandlers;

import com.nm.tapprofile.tapProfileContext.application.commands.PublishProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.shared.result.Result;
import com.nm.tapprofile.tapProfileContext.shared.result.Unit;
import com.nm.tapprofile.tapProfileContext.shared.time.DateTimeProvider;

public final class PublishProfileCommandHandler {

	private final ProfileRepository profileRepository;
	private final DateTimeProvider dateTimeProvider;

	public PublishProfileCommandHandler(
			ProfileRepository profileRepository,
			DateTimeProvider dateTimeProvider) {
		this.profileRepository = profileRepository;
		this.dateTimeProvider = dateTimeProvider;
	}

	public Result<DomainError, Unit> handle(PublishProfileCommand command) {
		ProfileId profileId = new ProfileId(command.profileId());

		var maybeProfile = profileRepository.findById(profileId);

		if (maybeProfile.isEmpty()) {
			return Result.failure(new ProfileNotFoundError(profileId.value()));
		}

		var publicationResult = maybeProfile.get().publish(dateTimeProvider.now());

		if (publicationResult.isFailure()) {
			return Result.failure(publicationResult.getFailure());
		}

		profileRepository.save(publicationResult.getSuccess());
		return Result.success(Unit.INSTANCE);
	}
}
